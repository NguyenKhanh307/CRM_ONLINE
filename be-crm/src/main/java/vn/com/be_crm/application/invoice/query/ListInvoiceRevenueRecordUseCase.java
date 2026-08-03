package vn.com.be_crm.application.invoice.query;

import vn.com.be_crm.application.invoice.dto.InvoiceRevenueRecordResult;
import vn.com.be_crm.application.invoice.mapper.InvoiceRevenueRecordCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRevenueRecordRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy danh sách bản ghi doanh thu theo invoiceId. */
public class ListInvoiceRevenueRecordUseCase implements IUseCase<Long, List<InvoiceRevenueRecordResult>> {
    private final IInvoiceRevenueRecordRepository repo;
    /** @param repo port lưu trữ */
    public ListInvoiceRevenueRecordUseCase(IInvoiceRevenueRecordRepository repo) { this.repo = repo; }
    /** Lấy danh sách InvoiceRevenueRecord theo invoiceId. @param invoiceId @return danh sách */
    @Override public List<InvoiceRevenueRecordResult> execute(Long invoiceId) {
        return repo.findAllByInvoiceId(invoiceId).stream().map(InvoiceRevenueRecordCommandMapper::toResult).collect(Collectors.toList());
    }
}
