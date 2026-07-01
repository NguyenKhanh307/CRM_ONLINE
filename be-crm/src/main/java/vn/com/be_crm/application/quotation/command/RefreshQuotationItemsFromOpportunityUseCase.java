package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.application.quotation.dto.QuotationResult;
import vn.com.be_crm.application.quotation.mapper.QuotationCommandMapper;
import vn.com.be_crm.application.shared.util.LineItemTotals;
import vn.com.be_crm.domain.opportunity.entity.OpportunityItem;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityItemRepository;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.entity.QuotationItem;
import vn.com.be_crm.domain.quotation.repository.IQuotationItemRepository;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;
import vn.com.be_crm.domain.shared.exception.DomainException;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Use case cập nhật lại danh sách dòng hàng của báo giá theo cơ hội nguồn:
 * xóa toàn bộ dòng báo giá hiện tại rồi tạo lại từ dòng hàng cơ hội (OLI → QLI),
 * giữ liên kết {@code opportunityItemId} để đồng bộ hai chiều vẫn hoạt động.
 */
public class RefreshQuotationItemsFromOpportunityUseCase {
    private final IQuotationRepository quotationRepo;
    private final IQuotationItemRepository quotationItemRepo;
    private final IOpportunityItemRepository opportunityItemRepo;

    /** @param quotationRepo báo giá @param quotationItemRepo dòng báo giá @param opportunityItemRepo dòng cơ hội */
    public RefreshQuotationItemsFromOpportunityUseCase(IQuotationRepository quotationRepo,
                                                       IQuotationItemRepository quotationItemRepo,
                                                       IOpportunityItemRepository opportunityItemRepo) {
        this.quotationRepo = quotationRepo;
        this.quotationItemRepo = quotationItemRepo;
        this.opportunityItemRepo = opportunityItemRepo;
    }

    /**
     * Cập nhật dòng hàng báo giá theo cơ hội (cơ hội là nguồn — không sync ngược).
     * @param quotationId ID báo giá @return báo giá sau cập nhật
     */
    public QuotationResult execute(Long quotationId) {
        Quotation q = quotationRepo.findById(quotationId)
                .orElseThrow(() -> new NotFoundException("Quotation not found: " + quotationId));
        if (q.isLocked()) {
            throw new DomainException("Báo giá đã khóa, không cập nhật được dòng hàng");
        }
        if (q.getOpportunityId() == null) {
            throw new DomainException("Báo giá không gắn cơ hội");
        }

        List<OpportunityItem> oppItems = opportunityItemRepo.findAllByOpportunityId(q.getOpportunityId());

        // Xóa toàn bộ dòng báo giá hiện tại
        for (QuotationItem existing : quotationItemRepo.findAllByQuotationId(quotationId)) {
            quotationItemRepo.deleteById(existing.getId());
        }

        // Tạo lại dòng báo giá từ dòng cơ hội, giữ tham chiếu nguồn (opportunityItemId)
        List<QuotationItem> newItems = new ArrayList<>();
        for (OpportunityItem oi : oppItems) {
            newItems.add(quotationItemRepo.save(QuotationItem.builder()
                    .quotationId(quotationId)
                    .productId(oi.getProductId())
                    .opportunityItemId(oi.getId())
                    .quantity(oi.getQuantity())
                    .unitPrice(oi.getUnitPrice())
                    .discount(oi.getDiscount())
                    .taxRate(BigDecimal.ZERO)
                    .amount(oi.getAmount())
                    .build()));
        }

        // Tính lại tổng tiền báo giá theo dòng hàng mới
        BigDecimal total = LineItemTotals.sumAmount(newItems, QuotationItem::getAmount);
        Quotation saved = quotationRepo.save(q.toBuilder()
                .subtotal(total).discount(BigDecimal.ZERO).tax(BigDecimal.ZERO).total(total)
                .build());
        return QuotationCommandMapper.toResult(saved);
    }
}
