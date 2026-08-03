package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.application.quotation.dto.UpdateQuotationItemCommand;
import vn.com.be_crm.application.quotation.dto.QuotationItemResult;
import vn.com.be_crm.application.quotation.mapper.QuotationItemCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.core.util.LineItemTotals;
import vn.com.be_crm.domain.quotation.entity.QuotationItem;
import vn.com.be_crm.domain.quotation.repository.IQuotationItemRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/** Use case cập nhật dòng hàng báo giá. Thành tiền do server tính; tổng chứng từ được tính lại sau khi lưu. */
public class UpdateQuotationItemUseCase implements IUseCase<UpdateQuotationItemCommand, QuotationItemResult> {
    private final IQuotationItemRepository repo;
    private final RecomputeQuotationTotalsUseCase recomputeUC;

    /** @param repo port lưu trữ @param recomputeUC tính lại tổng tiền chứng từ */
    public UpdateQuotationItemUseCase(IQuotationItemRepository repo, RecomputeQuotationTotalsUseCase recomputeUC) {
        this.repo = repo;
        this.recomputeUC = recomputeUC;
    }

    /** Cập nhật QuotationItem (thành tiền server tính) rồi tính lại tổng chứng từ. @param cmd @return QuotationItemResult @throws NotFoundException */
    @Override public QuotationItemResult execute(UpdateQuotationItemCommand cmd) {
        QuotationItem existing = repo.findById(cmd.getId())
                .orElseThrow(() -> new NotFoundException("QuotationItem not found: " + cmd.getId()));
        QuotationItem merged = QuotationItemCommandMapper.toEntity(cmd, existing);
        QuotationItem saved = repo.save(merged.toBuilder()
                .amount(LineItemTotals.lineAmount(merged.getQuantity(), merged.getUnitPrice(),
                        merged.getDiscount(), merged.getTaxRate()))
                .build());
        recomputeUC.execute(saved.getQuotationId());
        return QuotationItemCommandMapper.toResult(saved);
    }
}
