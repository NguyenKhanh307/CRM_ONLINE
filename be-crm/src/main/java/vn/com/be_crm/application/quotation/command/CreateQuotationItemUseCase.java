package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.application.quotation.dto.CreateQuotationItemCommand;
import vn.com.be_crm.application.quotation.dto.QuotationItemResult;
import vn.com.be_crm.application.quotation.mapper.QuotationItemCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.core.util.LineItemTotals;
import vn.com.be_crm.domain.quotation.entity.QuotationItem;
import vn.com.be_crm.domain.quotation.repository.IQuotationItemRepository;

/** Use case tạo mới dòng hàng báo giá. Thành tiền do server tính; tổng chứng từ được tính lại sau khi lưu. */
public class CreateQuotationItemUseCase implements IUseCase<CreateQuotationItemCommand, QuotationItemResult> {
    private final IQuotationItemRepository repo;
    private final RecomputeQuotationTotalsUseCase recomputeUC;

    /** @param repo port lưu trữ @param recomputeUC tính lại tổng tiền chứng từ */
    public CreateQuotationItemUseCase(IQuotationItemRepository repo, RecomputeQuotationTotalsUseCase recomputeUC) {
        this.repo = repo;
        this.recomputeUC = recomputeUC;
    }

    /** Tạo mới QuotationItem (thành tiền server tính) rồi tính lại tổng chứng từ. @param cmd @return QuotationItemResult */
    @Override public QuotationItemResult execute(CreateQuotationItemCommand cmd) {
        QuotationItem e = QuotationItemCommandMapper.toEntity(cmd);
        QuotationItem saved = repo.save(e.toBuilder()
                .amount(LineItemTotals.lineAmount(e.getQuantity(), e.getUnitPrice(), e.getDiscount(), e.getTaxRate()))
                .build());
        recomputeUC.execute(saved.getQuotationId());
        return QuotationItemCommandMapper.toResult(saved);
    }
}
