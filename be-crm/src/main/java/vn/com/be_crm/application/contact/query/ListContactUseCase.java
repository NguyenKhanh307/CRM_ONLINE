package vn.com.be_crm.application.contact.query;

import vn.com.be_crm.application.contact.dto.ContactResult;
import vn.com.be_crm.application.contact.mapper.ContactCommandMapper;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.page.PageResult;
import vn.com.be_crm.core.lookup.port.INameResolver;
import vn.com.be_crm.core.lookup.NameEnricher;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.contact.repository.IContactRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy danh sách liên hệ có phân trang. */
public class ListContactUseCase implements IUseCase<PageRequest, PageResult<ContactResult>> {
    private final IContactRepository repo;
    private final INameResolver names;
    /** @param repo port lưu trữ @param names port tra tên khóa ngoại */
    public ListContactUseCase(IContactRepository repo, INameResolver names) {
        this.repo = repo; this.names = names;
    }

    /**
     * Lấy danh sách Contact có phân trang, kèm tên khóa ngoại (khách hàng, người phụ trách).
     * @param r tham số phân trang @return PageResult
     */
    @Override
    public PageResult<ContactResult> execute(PageRequest r) {
        var page = repo.findAll(r);
        List<ContactResult> items = page.getItems().stream().map(ContactCommandMapper::toResult).collect(Collectors.toList());
        NameEnricher.apply(items, ContactResult::getCustomerId, names::customers, ContactResult::setCustomerName);
        NameEnricher.apply(items, ContactResult::getAssignedUserId, names::users, ContactResult::setAssignedUserName);
        NameEnricher.apply(items, ContactResult::getCreatedBy, names::users, ContactResult::setCreatedByName);
        NameEnricher.apply(items, ContactResult::getUpdatedBy, names::users, ContactResult::setUpdatedByName);
        return PageResult.<ContactResult>builder()
                .items(items)
                .total(page.getTotal()).page(page.getPage()).size(page.getSize()).build();
    }
}
