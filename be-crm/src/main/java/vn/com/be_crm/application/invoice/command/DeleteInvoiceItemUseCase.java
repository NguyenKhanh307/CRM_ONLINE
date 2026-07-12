package vn.com.be_crm.application.invoice.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.invoice.entity.InvoiceItem;
import vn.com.be_crm.domain.invoice.repository.IInvoiceItemRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case xóa dòng hàng hóa đơn. Tổng chứng từ được tính lại sau khi xóa. */
public class DeleteInvoiceItemUseCase implements IUseCase<Long, Void> {
    private final IInvoiceItemRepository repo;
    private final RecomputeInvoiceTotalsUseCase recomputeUC;

    /** @param repo port lưu trữ @param recomputeUC tính lại tổng tiền chứng từ */
    public DeleteInvoiceItemUseCase(IInvoiceItemRepository repo, RecomputeInvoiceTotalsUseCase recomputeUC) {
        this.repo = repo;
        this.recomputeUC = recomputeUC;
    }

    /** Xóa InvoiceItem rồi tính lại tổng chứng từ. @param id @return null @throws NotFoundException */
    @Override public Void execute(Long id) {
        InvoiceItem e = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("InvoiceItem not found: " + id));
        Long parentId = e.getInvoiceId();
        repo.deleteById(id);
        recomputeUC.execute(parentId);
        return null;
    }
}
