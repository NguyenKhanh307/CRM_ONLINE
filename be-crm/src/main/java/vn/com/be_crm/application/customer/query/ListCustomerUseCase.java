package vn.com.be_crm.application.customer.query;

import vn.com.be_crm.application.customer.dto.CustomerResult;
import vn.com.be_crm.application.customer.mapper.CustomerCommandMapper;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.page.PageResult;
import vn.com.be_crm.core.lookup.port.INameResolver;
import vn.com.be_crm.core.lookup.NameEnricher;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.customer.repository.ICustomerRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy danh sách khách hàng có phân trang. */
public class ListCustomerUseCase implements IUseCase<PageRequest, PageResult<CustomerResult>> {
    private final ICustomerRepository repo;
    private final INameResolver names;
    /** @param repo port lưu trữ @param names port tra tên khóa ngoại */
    public ListCustomerUseCase(ICustomerRepository repo, INameResolver names) { this.repo = repo; this.names = names; }

    /**
     * Lấy danh sách Customer có phân trang, kèm tên khóa ngoại (người phụ trách, đơn vị).
     * @param r tham số phân trang @return PageResult
     */
    @Override
    public PageResult<CustomerResult> execute(PageRequest r) {
        var page = repo.findAll(r);
        List<CustomerResult> items = page.getItems().stream().map(CustomerCommandMapper::toResult).collect(Collectors.toList());
        NameEnricher.apply(items, CustomerResult::getOwnerId, names::users, CustomerResult::setOwnerName);
        NameEnricher.apply(items, CustomerResult::getCreatedBy, names::users, CustomerResult::setCreatedByName);
        NameEnricher.apply(items, CustomerResult::getUpdatedBy, names::users, CustomerResult::setUpdatedByName);
        return PageResult.<CustomerResult>builder()
                .items(items)
                .total(page.getTotal()).page(page.getPage()).size(page.getSize()).build();
    }
}
