package vn.com.be_crm.application.auth.query;

import vn.com.be_crm.application.auth.dto.PermissionResult;
import vn.com.be_crm.application.auth.mapper.PermissionCommandMapper;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.auth.repository.IPermissionRepository;

import java.util.stream.Collectors;

/**
 * Use case lấy danh sách quyền hạn có phân trang.
 */
public class ListPermissionUseCase implements IUseCase<PageRequest, PageResult<PermissionResult>> {

    private final IPermissionRepository repository;

    /** @param repository port lưu trữ Permission */
    public ListPermissionUseCase(IPermissionRepository repository) {
        this.repository = repository;
    }

    /**
     * Lấy danh sách Permission theo tham số phân trang.
     *
     * @param request tham số phân trang
     * @return PageResult chứa danh sách PermissionResult
     */
    @Override
    public PageResult<PermissionResult> execute(PageRequest request) {
        var page = repository.findAll(request);
        return PageResult.<PermissionResult>builder()
                .items(page.getItems().stream().map(PermissionCommandMapper::toResult).collect(Collectors.toList()))
                .total(page.getTotal())
                .page(page.getPage())
                .size(page.getSize())
                .build();
    }
}
