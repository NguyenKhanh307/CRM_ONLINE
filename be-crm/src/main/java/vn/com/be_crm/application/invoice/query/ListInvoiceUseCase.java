package vn.com.be_crm.application.invoice.query;

import vn.com.be_crm.application.invoice.dto.InvoiceResult;
import vn.com.be_crm.application.invoice.mapper.InvoiceCommandMapper;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRepository;

import java.util.stream.Collectors;

/** Use case lấy danh sách đơn hàng có phân trang. */
public class ListInvoiceUseCase implements IUseCase<PageRequest, PageResult<InvoiceResult>> {
    private final IInvoiceRepository repo;
    /** @param repo port lưu trữ */
    public ListInvoiceUseCase(IInvoiceRepository repo) { this.repo = repo; }
    /** Lấy danh sách Invoice. @param r phân trang @return PageResult */
    @Override public PageResult<InvoiceResult> execute(PageRequest r) {
        var page = repo.findAll(r);
        return PageResult.<InvoiceResult>builder()
                .items(page.getItems().stream().map(InvoiceCommandMapper::toResult).collect(Collectors.toList()))
                .total(page.getTotal()).page(page.getPage()).size(page.getSize()).build();
    }
}
