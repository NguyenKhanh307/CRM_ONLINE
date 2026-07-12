package vn.com.be_crm.application.invoice.command;

import vn.com.be_crm.application.invoice.dto.UpdateInvoiceItemCommand;
import vn.com.be_crm.application.invoice.dto.InvoiceItemResult;
import vn.com.be_crm.application.invoice.mapper.InvoiceItemCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.application.shared.util.LineItemTotals;
import vn.com.be_crm.domain.invoice.entity.InvoiceItem;
import vn.com.be_crm.domain.invoice.repository.IInvoiceItemRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case cập nhật dòng hàng hóa đơn. Thành tiền do server tính; tổng chứng từ được tính lại sau khi lưu. */
public class UpdateInvoiceItemUseCase implements IUseCase<UpdateInvoiceItemCommand, InvoiceItemResult> {
    private final IInvoiceItemRepository repo;
    private final RecomputeInvoiceTotalsUseCase recomputeUC;

    /** @param repo port lưu trữ @param recomputeUC tính lại tổng tiền chứng từ */
    public UpdateInvoiceItemUseCase(IInvoiceItemRepository repo, RecomputeInvoiceTotalsUseCase recomputeUC) {
        this.repo = repo;
        this.recomputeUC = recomputeUC;
    }

    /** Cập nhật InvoiceItem (thành tiền server tính) rồi tính lại tổng chứng từ. @param cmd @return InvoiceItemResult @throws NotFoundException */
    @Override public InvoiceItemResult execute(UpdateInvoiceItemCommand cmd) {
        InvoiceItem existing = repo.findById(cmd.getId())
                .orElseThrow(() -> new NotFoundException("InvoiceItem not found: " + cmd.getId()));
        InvoiceItem merged = InvoiceItemCommandMapper.toEntity(cmd, existing);
        InvoiceItem saved = repo.save(merged.toBuilder()
                .amount(LineItemTotals.lineAmount(merged.getQuantity(), merged.getUnitPrice(),
                        merged.getDiscount(), merged.getTaxRate()))
                .build());
        recomputeUC.execute(saved.getInvoiceId());
        return InvoiceItemCommandMapper.toResult(saved);
    }
}
