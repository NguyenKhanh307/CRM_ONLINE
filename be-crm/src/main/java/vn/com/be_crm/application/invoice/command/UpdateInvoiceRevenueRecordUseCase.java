package vn.com.be_crm.application.invoice.command;

import vn.com.be_crm.application.invoice.dto.InvoiceRevenueRecordResult;
import vn.com.be_crm.application.invoice.dto.UpdateInvoiceRevenueRecordCommand;
import vn.com.be_crm.application.invoice.mapper.InvoiceRevenueRecordCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.invoice.entity.InvoiceRevenueRecord;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRevenueRecordRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case cập nhật bản ghi doanh thu đơn hàng. */
public class UpdateInvoiceRevenueRecordUseCase implements IUseCase<UpdateInvoiceRevenueRecordCommand, InvoiceRevenueRecordResult> {
    private final IInvoiceRevenueRecordRepository repo;
    /** @param repo port lưu trữ */
    public UpdateInvoiceRevenueRecordUseCase(IInvoiceRevenueRecordRepository repo) { this.repo = repo; }
    /** Cập nhật InvoiceRevenueRecord. @param cmd @return InvoiceRevenueRecordResult @throws NotFoundException */
    @Override public InvoiceRevenueRecordResult execute(UpdateInvoiceRevenueRecordCommand cmd) {
        InvoiceRevenueRecord e = repo.findById(cmd.getId()).orElseThrow(() -> new NotFoundException("InvoiceRevenueRecord not found: " + cmd.getId()));
        return InvoiceRevenueRecordCommandMapper.toResult(repo.save(InvoiceRevenueRecordCommandMapper.toEntity(cmd, e)));
    }
}
