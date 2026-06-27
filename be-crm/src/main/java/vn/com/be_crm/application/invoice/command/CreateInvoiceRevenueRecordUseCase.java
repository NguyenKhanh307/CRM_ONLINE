package vn.com.be_crm.application.invoice.command;

import vn.com.be_crm.application.invoice.dto.CreateInvoiceRevenueRecordCommand;
import vn.com.be_crm.application.invoice.dto.InvoiceRevenueRecordResult;
import vn.com.be_crm.application.invoice.mapper.InvoiceRevenueRecordCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRevenueRecordRepository;

/** Use case tạo mới bản ghi doanh thu đơn hàng. */
public class CreateInvoiceRevenueRecordUseCase implements IUseCase<CreateInvoiceRevenueRecordCommand, InvoiceRevenueRecordResult> {
    private final IInvoiceRevenueRecordRepository repo;
    /** @param repo port lưu trữ */
    public CreateInvoiceRevenueRecordUseCase(IInvoiceRevenueRecordRepository repo) { this.repo = repo; }
    /** Tạo mới InvoiceRevenueRecord. @param cmd @return InvoiceRevenueRecordResult */
    @Override public InvoiceRevenueRecordResult execute(CreateInvoiceRevenueRecordCommand cmd) {
        return InvoiceRevenueRecordCommandMapper.toResult(repo.save(InvoiceRevenueRecordCommandMapper.toEntity(cmd)));
    }
}
