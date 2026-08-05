package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.application.quotation.dto.QuotationResult;
import vn.com.be_crm.application.quotation.mapper.QuotationCommandMapper;
import vn.com.be_crm.core.util.LineItemTotals;
import vn.com.be_crm.domain.opportunity.entity.OpportunityItem;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityItemRepository;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.entity.QuotationItem;
import vn.com.be_crm.domain.quotation.repository.IQuotationItemRepository;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;
import vn.com.be_crm.core.error.frontend.DomainException;
import vn.com.be_crm.core.error.frontend.NotFoundException;
import vn.com.be_crm.core.tx.port.ITransactionRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

// cập nhật lại danh sách dòng hàng của báo giá theo cơ hội nguồn: xóa toàn bộ dòng báo giá hiện
// tại rồi tạo lại từ dòng hàng cơ hội (OLI -> QLI), giữ liên kết opportunityItemId để đồng bộ
// hai chiều vẫn hoạt động
public class RefreshQuotationItemsFromOpportunityUseCase {
    private final IQuotationRepository quotationRepo;
    private final IQuotationItemRepository quotationItemRepo;
    private final IOpportunityItemRepository opportunityItemRepo;
    private final ITransactionRunner tx;

    public RefreshQuotationItemsFromOpportunityUseCase(IQuotationRepository quotationRepo,
                                                       IQuotationItemRepository quotationItemRepo,
                                                       IOpportunityItemRepository opportunityItemRepo,
                                                       ITransactionRunner tx) {
        this.quotationRepo = quotationRepo;
        this.quotationItemRepo = quotationItemRepo;
        this.opportunityItemRepo = opportunityItemRepo;
        this.tx = tx;
    }

    // xóa dòng cũ + tạo lại chạy trong MỘT transaction (không để báo giá mất dòng hàng giữa chừng)
    public QuotationResult execute(Long quotationId) {
        return tx.call(() -> executeInTx(quotationId));
    }

    // thân nghiệp vụ refresh — luôn chạy bên trong transaction
    private QuotationResult executeInTx(Long quotationId) {
        Quotation q = quotationRepo.findById(quotationId)
                .orElseThrow(() -> new NotFoundException("Quotation not found: " + quotationId));
        if (q.isLocked()) {
            throw new DomainException("Báo giá đã khóa, không cập nhật được dòng hàng");
        }
        if (q.getOpportunityId() == null) {
            throw new DomainException("Báo giá không gắn cơ hội");
        }

        List<OpportunityItem> oppItems = opportunityItemRepo.findAllByOpportunityId(q.getOpportunityId());

        // xóa toàn bộ dòng báo giá hiện tại
        for (QuotationItem existing : quotationItemRepo.findAllByQuotationId(quotationId)) {
            quotationItemRepo.deleteById(existing.getId());
        }

        // tạo lại dòng báo giá từ dòng cơ hội, giữ tham chiếu nguồn (opportunityItemId)
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
                    .build()));
        }

        QuotationResult result = QuotationCommandMapper.toResult(q);
        LineItemTotals.Totals t = LineItemTotals.compute(newItems,
                QuotationItem::getQuantity, QuotationItem::getUnitPrice, QuotationItem::getDiscount, QuotationItem::getTaxRate);
        result.setSubtotal(t.subtotal()); result.setDiscount(t.discount());
        result.setTax(t.tax()); result.setTotal(t.total());
        return result;
    }
}
