package vn.com.be_crm.application.customer.query;

import vn.com.be_crm.application.customer.dto.CustomerResult;
import vn.com.be_crm.application.customer.mapper.CustomerCommandMapper;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.customer.repository.ICustomerRepository;

import java.util.stream.Collectors;

/** Use case lấy danh sách khách hàng có phân trang. */
public class ListCustomerUseCase implements IUseCase<PageRequest, PageResult<CustomerResult>> {
    private final ICustomerRepository repo;
    /** @param repo port lưu trữ */
    public ListCustomerUseCase(ICustomerRepository repo) { this.repo = repo; }

    /**
     * Lấy danh sách Customer có phân trang.
     * @param r tham số phân trang @return PageResult
     */
    @Override
    public PageResult<CustomerResult> execute(PageRequest r) {
        var page = repo.findAll(r);
        return PageResult.<CustomerResult>builder()
                .items(page.getItems().stream().map(CustomerCommandMapper::toResult).collect(Collectors.toList()))
                .total(page.getTotal()).page(page.getPage()).size(page.getSize()).build();
    }
}
