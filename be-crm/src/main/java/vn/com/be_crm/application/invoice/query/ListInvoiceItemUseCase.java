package vn.com.be_crm.application.invoice.query;

import vn.com.be_crm.application.invoice.dto.InvoiceItemResult;
import vn.com.be_crm.application.invoice.mapper.InvoiceItemCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.invoice.repository.IInvoiceItemRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy danh sách dòng đơn hàng theo invoiceId. */
public class ListInvoiceItemUseCase implements IUseCase<Long, List<InvoiceItemResult>> {
    private final IInvoiceItemRepository repo;
    /** @param repo port lưu trữ */
    public ListInvoiceItemUseCase(IInvoiceItemRepository repo) { this.repo = repo; }
    /** Lấy danh sách InvoiceItem theo invoiceId. @param invoiceId @return danh sách */
    @Override public List<InvoiceItemResult> execute(Long invoiceId) {
        return repo.findAllByInvoiceId(invoiceId).stream().map(InvoiceItemCommandMapper::toResult).collect(Collectors.toList());
    }
}
