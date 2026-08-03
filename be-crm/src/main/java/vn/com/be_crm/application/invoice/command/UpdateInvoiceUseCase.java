package vn.com.be_crm.application.invoice.command;

import vn.com.be_crm.core.util.CrossFieldRules;
import vn.com.be_crm.application.invoice.dto.InvoiceResult;
import vn.com.be_crm.application.invoice.dto.UpdateInvoiceCommand;
import vn.com.be_crm.application.invoice.mapper.InvoiceCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.invoice.entity.Invoice;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/** Use case cập nhật hóa đơn. */
public class UpdateInvoiceUseCase implements IUseCase<UpdateInvoiceCommand, InvoiceResult> {
    private final IInvoiceRepository repo;
    private final RecomputeInvoiceTotalsUseCase recomputeUC;

    /** @param repo port lưu trữ @param recomputeUC tính lại tổng tiền từ dòng hàng */
    public UpdateInvoiceUseCase(IInvoiceRepository repo, RecomputeInvoiceTotalsUseCase recomputeUC) {
        this.repo = repo;
        this.recomputeUC = recomputeUC;
    }

    /**
     * Cập nhật Invoice. Tổng tiền KHÔNG lấy từ client mà tính lại từ dòng hàng sau khi lưu.
     * @param cmd @return InvoiceResult @throws NotFoundException
     */
    @Override public InvoiceResult execute(UpdateInvoiceCommand cmd) {
        // Ràng buộc khoảng thời gian: hạn thanh toán không được trước ngày hóa đơn
        CrossFieldRules.requireDateRange(cmd.getInvoiceDate(), cmd.getDueDate(), "Ngày hóa đơn", "Hạn thanh toán");
        Invoice e = repo.findById(cmd.getId()).orElseThrow(() -> new NotFoundException("Invoice not found: " + cmd.getId()));
        repo.save(InvoiceCommandMapper.toEntity(cmd, e));
        recomputeUC.execute(cmd.getId());
        return InvoiceCommandMapper.toResult(
                repo.findById(cmd.getId()).orElseThrow(() -> new NotFoundException("Invoice not found: " + cmd.getId())));
    }
}
