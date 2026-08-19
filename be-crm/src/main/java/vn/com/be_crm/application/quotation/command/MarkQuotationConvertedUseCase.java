package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.application.quotation.dto.QuotationResult;
import vn.com.be_crm.application.quotation.mapper.QuotationCommandMapper;
import vn.com.be_crm.domain.opportunity.entity.Opportunity;
import vn.com.be_crm.domain.opportunity.enums.OpportunityStatus;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;
import vn.com.be_crm.core.error.frontend.DomainException;
import vn.com.be_crm.core.error.frontend.NotFoundException;
import vn.com.be_crm.core.tx.port.ITransactionRunner;

// đánh dấu báo giá đã được nhân viên tự tay chuyển thành đơn hàng (FE đã tạo Đơn hàng riêng qua
// AddPage, dòng hàng do FE tự điền — xem OrderAddPage?fromQuotation=). Use case này CHỈ còn khóa
// báo giá (audit trail) + chuyển cơ hội liên quan sang Chốt Thắng (won), không tạo bản ghi nào.
// Luồng khách tự đồng ý qua trang công khai vẫn dùng ConvertQuotationToOrderUseCase riêng (tự
// sinh cả đơn hàng vì không có ai ngồi điền AddPage).
public class MarkQuotationConvertedUseCase {
    private final IQuotationRepository quotationRepo;
    private final IOpportunityRepository opportunityRepo;
    private final ITransactionRunner tx;

    public MarkQuotationConvertedUseCase(IQuotationRepository quotationRepo, IOpportunityRepository opportunityRepo,
                                          ITransactionRunner tx) {
        this.quotationRepo = quotationRepo;
        this.opportunityRepo = opportunityRepo;
        this.tx = tx;
    }

    // khóa báo giá + chốt thắng cơ hội — chạy trong MỘT transaction
    public QuotationResult execute(Long quotationId) {
        return tx.call(() -> executeInTx(quotationId));
    }

    private QuotationResult executeInTx(Long quotationId) {
        Quotation q = quotationRepo.findById(quotationId)
                .orElseThrow(() -> new NotFoundException("Quotation not found: " + quotationId));
        if (q.isLocked()) throw new DomainException("Báo giá đã được chuyển thành đơn hàng trước đó");

        Quotation locked = quotationRepo.save(q.toBuilder().isLocked(true).build());

        if (q.getOpportunityId() != null) {
            Opportunity opp = opportunityRepo.findById(q.getOpportunityId()).orElse(null);
            if (opp != null && opp.getStatus() != OpportunityStatus.won) {
                opportunityRepo.save(opp.toBuilder().status(OpportunityStatus.won).build());
            }
        }
        return QuotationCommandMapper.toResult(locked);
    }
}
