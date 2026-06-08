package vn.com.be_crm.application.auth.query;

import vn.com.be_crm.application.auth.dto.RoleResult;
import vn.com.be_crm.application.auth.mapper.RoleCommandMapper;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.auth.repository.IRoleRepository;

import java.util.stream.Collectors;

/**
 * Use case lấy danh sách vai trò có phân trang.
 */
public class ListRoleUseCase implements IUseCase<PageRequest, PageResult<RoleResult>> {

    private final IRoleRepository repository;

    /** @param repository port lưu trữ Role */
    public ListRoleUseCase(IRoleRepository repository) {
        this.repository = repository;
    }

    /**
     * Lấy danh sách Role theo tham số phân trang.
     *
     * @param request tham số phân trang
     * @return PageResult chứa danh sách RoleResult
     */
    @Override
    public PageResult<RoleResult> execute(PageRequest request) {
        var page = repository.findAll(request);
        return PageResult.<RoleResult>builder()
                .items(page.getItems().stream().map(RoleCommandMapper::toResult).collect(Collectors.toList()))
                .total(page.getTotal())
                .page(page.getPage())
                .size(page.getSize())
                .build();
    }
}
