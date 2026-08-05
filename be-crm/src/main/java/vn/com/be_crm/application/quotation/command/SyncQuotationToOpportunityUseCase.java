package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.application.opportunity.command.RecomputeOpportunityAmountUseCase;
import vn.com.be_crm.core.util.LineItemTotals;
import vn.com.be_crm.domain.opportunity.entity.OpportunityItem;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityItemRepository;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.entity.QuotationItem;
import vn.com.be_crm.domain.quotation.repository.IQuotationItemRepository;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;
import vn.com.be_crm.core.tx.port.ITransactionRunner;

// đồng bộ hai chiều: khi dòng hàng của báo giá ĐỒNG BỘ (primary, chưa khóa) thay đổi, cập nhật
// ngược lại dòng hàng cơ hội tương ứng rồi tính lại giá trị cơ hội (roll-up)
public class SyncQuotationToOpportunityUseCase {
    private final IQuotationRepository quotationRepo;
    private final IQuotationItemRepository quotationItemRepo;
    private final IOpportunityItemRepository opportunityItemRepo;
    private final RecomputeOpportunityAmountUseCase recomputeUC;
    private final ITransactionRunner tx;

    public SyncQuotationToOpportunityUseCase(IQuotationRepository quotationRepo, IQuotationItemRepository quotationItemRepo,
                                             IOpportunityItemRepository opportunityItemRepo,
                                             RecomputeOpportunityAmountUseCase recomputeUC, ITransactionRunner tx) {
        this.quotationRepo = quotationRepo;
        this.quotationItemRepo = quotationItemRepo;
        this.opportunityItemRepo = opportunityItemRepo;
        this.recomputeUC = recomputeUC;
        this.tx = tx;
    }

    // cập nhật N dòng cơ hội + roll-up giá trị cơ hội chạy trong MỘT transaction (không để cơ
    // hội có dòng hàng cập nhật dở dang mà tổng tiền chưa tính lại)
    public void execute(Long quotationId) {
        tx.run(() -> {
            Quotation q = quotationRepo.findById(quotationId).orElse(null);
            // chỉ sync khi là báo giá đồng bộ, chưa khóa và gắn cơ hội
            if (q == null || !q.isPrimary() || q.isLocked() || q.getOpportunityId() == null) return;

            for (QuotationItem qi : quotationItemRepo.findAllByQuotationId(quotationId)) {
                if (qi.getOpportunityItemId() == null) continue;
                OpportunityItem oi = opportunityItemRepo.findById(qi.getOpportunityItemId()).orElse(null);
                if (oi == null) continue;
                // ghi đè số lượng/đơn giá/chiết khấu/thành tiền từ dòng báo giá xuống dòng cơ hội
                opportunityItemRepo.save(OpportunityItem.builder()
                        .id(oi.getId()).opportunityId(oi.getOpportunityId()).productId(oi.getProductId())
                        .quantity(qi.getQuantity()).unitPrice(qi.getUnitPrice()).discount(qi.getDiscount())
                        .amount(LineItemTotals.lineAmount(qi.getQuantity(), qi.getUnitPrice(), qi.getDiscount(), qi.getTaxRate()))
                        .note(oi.getNote())
                        .build());
            }
            recomputeUC.execute(q.getOpportunityId());
        });
    }
}
