package vn.com.be_crm.application.quotation.command;

import tools.jackson.databind.ObjectMapper;
import vn.com.be_crm.application.quotation.dto.QuotationAdjustmentProposal;
import vn.com.be_crm.application.quotation.dto.QuotationResult;
import vn.com.be_crm.application.quotation.mapper.QuotationCommandMapper;
import vn.com.be_crm.core.error.frontend.DomainException;
import vn.com.be_crm.core.error.frontend.NotFoundException;
import vn.com.be_crm.core.tx.port.ITransactionRunner;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.entity.QuotationItem;
import vn.com.be_crm.domain.quotation.enums.QuotationStatus;
import vn.com.be_crm.domain.quotation.repository.IQuotationItemRepository;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Nhân viên bấm "Tạo báo giá mới theo yêu cầu khách" trên báo giá đã nhận đề xuất chỉnh sửa (Q1,
// customerResponse="adjust", đề xuất mã hoá JSON trong customerResponseNote — xem
// ProposeQuotationAdjustmentUseCase). Sinh báo giá MỚI (Q2) kế thừa header từ Q1 + dòng hàng đã áp
// số lượng khách đề nghị (dòng khách xóa thì không mang sang). Q1 KHÔNG bị sửa nội dung — chỉ khóa
// lại (is_locked=true) làm tín hiệu "đã có phiên bản kế tiếp", tái dùng đúng cờ có sẵn (giống cách
// báo giá bị khóa khi đã chuyển thành đơn hàng) — không thêm cột/bảng DB mới.
public class CreateQuotationFromAdjustmentUseCase {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final IQuotationRepository quotationRepo;
    private final IQuotationItemRepository itemRepo;
    private final ITransactionRunner tx;

    public CreateQuotationFromAdjustmentUseCase(IQuotationRepository quotationRepo, IQuotationItemRepository itemRepo,
                                                  ITransactionRunner tx) {
        this.quotationRepo = quotationRepo;
        this.itemRepo = itemRepo;
        this.tx = tx;
    }

    public QuotationResult execute(Long quotationId) {
        return tx.call(() -> executeInTx(quotationId));
    }

    private QuotationResult executeInTx(Long quotationId) {
        Quotation q1 = quotationRepo.findById(quotationId)
                .orElseThrow(() -> new NotFoundException("Quotation not found: " + quotationId));
        if (!"adjust".equals(q1.getCustomerResponse())) {
            throw new DomainException("Báo giá chưa có đề xuất chỉnh sửa nào từ khách");
        }
        if (q1.isLocked()) {
            throw new DomainException("Báo giá đã có phiên bản mới, không thể tạo lại");
        }

        QuotationAdjustmentProposal proposal;
        try {
            proposal = MAPPER.readValue(q1.getCustomerResponseNote(), QuotationAdjustmentProposal.class);
        } catch (Exception e) {
            throw new DomainException("Không đọc được đề xuất chỉnh sửa của khách");
        }
        if (proposal.items() == null || proposal.items().isEmpty()) {
            throw new DomainException("Đề xuất của khách không có dòng hàng nào");
        }
        Map<Long, BigDecimal> proposedQty = proposal.items().stream()
                .collect(Collectors.toMap(QuotationAdjustmentProposal.Item::id, QuotationAdjustmentProposal.Item::quantity));

        // dòng hàng Q2 = dòng hàng Q1 có mặt trong đề xuất, áp số lượng mới — giữ nguyên đơn giá/CK/thuế,
        // KHÔNG cho thêm sản phẩm mới ngoài danh sách cũ của Q1
        List<QuotationItem> q2Items = itemRepo.findAllByQuotationId(q1.getId()).stream()
                .filter(it -> proposedQty.containsKey(it.getId()))
                .map(it -> QuotationItem.builder()
                        .productId(it.getProductId())
                        .unit(it.getUnit())
                        .quantity(proposedQty.get(it.getId()))
                        .unitPrice(it.getUnitPrice())
                        .discount(it.getDiscount())
                        .taxRate(it.getTaxRate())
                        .build())
                .collect(Collectors.toList());
        if (q2Items.isEmpty()) {
            throw new DomainException("Không có dòng hàng hợp lệ trong đề xuất để tạo báo giá mới");
        }

        Quotation q2 = Quotation.builder()
                .code("BG-" + System.currentTimeMillis())
                .customerId(q1.getCustomerId()).contactId(q1.getContactId())
                .opportunityId(q1.getOpportunityId()).pricePolicyId(q1.getPricePolicyId())
                .ownerId(q1.getOwnerId())
                .quoteDate(LocalDate.now()).validUntil(q1.getValidUntil())
                .note(q1.getNote())
                .status(QuotationStatus.draft)
                .isPrimary(q1.isPrimary())
                .build();
        Quotation savedQ2 = quotationRepo.saveWithItems(q2, q2Items);

        // khóa Q1 — tín hiệu "đã có phiên bản kế tiếp", gỡ cờ primary (chuyển sang Q2)
        quotationRepo.save(q1.toBuilder().isLocked(true).isPrimary(false).build());

        return QuotationCommandMapper.toResult(savedQ2);
    }
}
