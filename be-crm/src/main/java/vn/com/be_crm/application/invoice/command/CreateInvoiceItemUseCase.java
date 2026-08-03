package vn.com.be_crm.application.invoice.command;

import vn.com.be_crm.application.invoice.dto.CreateInvoiceItemCommand;
import vn.com.be_crm.application.invoice.dto.InvoiceItemResult;
import vn.com.be_crm.application.invoice.mapper.InvoiceItemCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.core.util.LineItemTotals;
import vn.com.be_crm.domain.invoice.entity.InvoiceItem;
import vn.com.be_crm.domain.invoice.repository.IInvoiceItemRepository;

/** Use case tạo mới dòng hàng hóa đơn. Thành tiền do server tính; tổng chứng từ được tính lại sau khi lưu. */
public class CreateInvoiceItemUseCase implements IUseCase<CreateInvoiceItemCommand, InvoiceItemResult> {
    private final IInvoiceItemRepository repo;
    private final RecomputeInvoiceTotalsUseCase recomputeUC;

    /** @param repo port lưu trữ @param recomputeUC tính lại tổng tiền chứng từ */
    public CreateInvoiceItemUseCase(IInvoiceItemRepository repo, RecomputeInvoiceTotalsUseCase recomputeUC) {
        this.repo = repo;
        this.recomputeUC = recomputeUC;
    }

    /** Tạo mới InvoiceItem (thành tiền server tính) rồi tính lại tổng chứng từ. @param cmd @return InvoiceItemResult */
    @Override public InvoiceItemResult execute(CreateInvoiceItemCommand cmd) {
        InvoiceItem e = InvoiceItemCommandMapper.toEntity(cmd);
        InvoiceItem saved = repo.save(e.toBuilder()
                .amount(LineItemTotals.lineAmount(e.getQuantity(), e.getUnitPrice(), e.getDiscount(), e.getTaxRate()))
                .build());
        recomputeUC.execute(saved.getInvoiceId());
        return InvoiceItemCommandMapper.toResult(saved);
    }
}
