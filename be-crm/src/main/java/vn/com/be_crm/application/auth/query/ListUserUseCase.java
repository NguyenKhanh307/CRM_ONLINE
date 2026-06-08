package vn.com.be_crm.application.auth.query;

import vn.com.be_crm.application.auth.dto.UserResult;
import vn.com.be_crm.application.auth.mapper.UserCommandMapper;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.auth.repository.IUserRepository;

import java.util.stream.Collectors;

/**
 * Use case lấy danh sách người dùng có phân trang.
 */
public class ListUserUseCase implements IUseCase<PageRequest, PageResult<UserResult>> {

    private final IUserRepository repository;

    /** @param repository port lưu trữ User */
    public ListUserUseCase(IUserRepository repository) {
        this.repository = repository;
    }

    /**
     * Lấy danh sách User theo tham số phân trang.
     *
     * @param request tham số phân trang
     * @return PageResult chứa danh sách UserResult
     */
    @Override
    public PageResult<UserResult> execute(PageRequest request) {
        var page = repository.findAll(request);
        return PageResult.<UserResult>builder()
                .items(page.getItems().stream().map(UserCommandMapper::toResult).collect(Collectors.toList()))
                .total(page.getTotal())
                .page(page.getPage())
                .size(page.getSize())
                .build();
    }
}
