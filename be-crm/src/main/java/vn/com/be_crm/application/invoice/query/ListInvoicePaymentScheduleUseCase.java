package vn.com.be_crm.application.invoice.query;

import vn.com.be_crm.application.invoice.dto.InvoicePaymentScheduleResult;
import vn.com.be_crm.application.invoice.mapper.InvoicePaymentScheduleCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.invoice.repository.IInvoicePaymentScheduleRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy danh sách đợt thanh toán theo invoiceId. */
public class ListInvoicePaymentScheduleUseCase implements IUseCase<Long, List<InvoicePaymentScheduleResult>> {
    private final IInvoicePaymentScheduleRepository repo;
    /** @param repo port lưu trữ */
    public ListInvoicePaymentScheduleUseCase(IInvoicePaymentScheduleRepository repo) { this.repo = repo; }
    /** Lấy danh sách InvoicePaymentSchedule theo invoiceId. @param invoiceId @return danh sách */
    @Override public List<InvoicePaymentScheduleResult> execute(Long invoiceId) {
        return repo.findAllByInvoiceId(invoiceId).stream().map(InvoicePaymentScheduleCommandMapper::toResult).collect(Collectors.toList());
    }
}
