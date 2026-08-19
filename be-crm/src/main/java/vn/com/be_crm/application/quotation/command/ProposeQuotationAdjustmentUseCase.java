package vn.com.be_crm.application.quotation.command;

import tools.jackson.databind.ObjectMapper;
import vn.com.be_crm.application.notification.command.CreateNotificationUseCase;
import vn.com.be_crm.application.quotation.dto.QuotationAdjustmentProposal;
import vn.com.be_crm.core.error.frontend.DomainException;
import vn.com.be_crm.core.error.frontend.NotFoundException;
import vn.com.be_crm.core.tx.port.ITransactionRunner;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.entity.QuotationItem;
import vn.com.be_crm.domain.quotation.repository.IQuotationItemRepository;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// Khách "Chỉnh sửa" báo giá từ trang công khai (không đăng nhập) -> chỉ LƯU đề xuất, KHÔNG đụng
// quotation_items thật. Đề xuất mã hoá JSON (QuotationAdjustmentProposal) lưu vào cột có sẵn
// customer_response_note (không thêm cột/bảng DB mới — theo đúng yêu cầu). Nhân viên phụ trách bấm
// "Tạo báo giá mới theo yêu cầu khách" (CreateQuotationFromAdjustmentUseCase) mới thực sự áp đề xuất.
public class ProposeQuotationAdjustmentUseCase {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final IQuotationRepository quotationRepo;
    private final IQuotationItemRepository itemRepo;
    private final CreateNotificationUseCase createNotificationUC;
    private final ITransactionRunner tx;

    public ProposeQuotationAdjustmentUseCase(IQuotationRepository quotationRepo, IQuotationItemRepository itemRepo,
                                              CreateNotificationUseCase createNotificationUC, ITransactionRunner tx) {
        this.quotationRepo = quotationRepo;
        this.itemRepo = itemRepo;
        this.createNotificationUC = createNotificationUC;
        this.tx = tx;
    }

    // code: mã báo giá công khai; items: dòng hàng khách muốn giữ lại kèm SL mới; note: tuỳ chọn
    public void execute(String code, List<QuotationAdjustmentProposal.Item> items, String note) {
        tx.call(() -> {
            executeInTx(code, items, note);
            return null;
        });
    }

    private void executeInTx(String code, List<QuotationAdjustmentProposal.Item> items, String note) {
        Quotation q = quotationRepo.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy báo giá"));
        if (q.isLocked()) {
            throw new DomainException("Báo giá đã khóa, không thể đề xuất chỉnh sửa");
        }
        if (items == null || items.isEmpty()) {
            throw new DomainException("Vui lòng giữ lại ít nhất 1 dòng hàng");
        }

        // chặn id lạ/của báo giá khác — chỉ nhận dòng hàng thật sự thuộc báo giá này
        Set<Long> validIds = itemRepo.findAllByQuotationId(q.getId()).stream()
                .map(QuotationItem::getId).collect(Collectors.toSet());
        for (QuotationAdjustmentProposal.Item it : items) {
            if (it.id() == null || !validIds.contains(it.id())) {
                throw new DomainException("Dòng hàng trong đề xuất không hợp lệ");
            }
            if (it.quantity() == null || it.quantity().signum() <= 0) {
                throw new DomainException("Số lượng đề xuất phải lớn hơn 0");
            }
        }

        String json;
        try {
            json = MAPPER.writeValueAsString(new QuotationAdjustmentProposal(note, items));
        } catch (Exception e) {
            throw new DomainException("Không xử lý được đề xuất chỉnh sửa");
        }

        Quotation saved = quotationRepo.save(q.toBuilder()
                .customerResponse("adjust")
                .customerResponseNote(json)
                .build());

        if (saved.getOwnerId() != null) {
            createNotificationUC.execute(List.of(saved.getOwnerId()), "quotation_customer_response",
                    "Khách đề nghị chỉnh sửa báo giá: " + saved.getCode(),
                    "Khách hàng đề nghị chỉnh sửa dòng hàng báo giá " + saved.getCode()
                            + " — vào báo giá để tạo phiên bản mới.",
                    "quotation", saved.getId());
        }
    }
}
