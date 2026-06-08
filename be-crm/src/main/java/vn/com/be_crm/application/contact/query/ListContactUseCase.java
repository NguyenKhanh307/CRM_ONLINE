package vn.com.be_crm.application.contact.query;

import vn.com.be_crm.application.contact.dto.ContactResult;
import vn.com.be_crm.application.contact.mapper.ContactCommandMapper;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.contact.repository.IContactRepository;

import java.util.stream.Collectors;

/** Use case lấy danh sách liên hệ có phân trang. */
public class ListContactUseCase implements IUseCase<PageRequest, PageResult<ContactResult>> {
    private final IContactRepository repo;
    /** @param repo port lưu trữ */
    public ListContactUseCase(IContactRepository repo) { this.repo = repo; }

    /**
     * Lấy danh sách Contact có phân trang.
     * @param r tham số phân trang @return PageResult
     */
    @Override
    public PageResult<ContactResult> execute(PageRequest r) {
        var page = repo.findAll(r);
        return PageResult.<ContactResult>builder()
                .items(page.getItems().stream().map(ContactCommandMapper::toResult).collect(Collectors.toList()))
                .total(page.getTotal()).page(page.getPage()).size(page.getSize()).build();
    }
}
