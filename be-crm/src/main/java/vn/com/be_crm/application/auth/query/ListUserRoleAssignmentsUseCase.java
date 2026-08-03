package vn.com.be_crm.application.auth.query;

import vn.com.be_crm.application.auth.dto.UserRoleResult;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.auth.repository.IUserRoleRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Use case lấy toàn bộ liên kết user-role hiện có (bảng nhỏ, tải hết một lần).
 * FE dùng để lọc user đã thuộc một nhóm bất kỳ khỏi danh sách "thêm thành viên" của nhóm khác.
 */
public class ListUserRoleAssignmentsUseCase implements IUseCase<Void, List<UserRoleResult>> {

    private final IUserRoleRepository repository;

    /** @param repository port lưu trữ UserRole */
    public ListUserRoleAssignmentsUseCase(IUserRoleRepository repository) {
        this.repository = repository;
    }

    /**
     * Lấy toàn bộ liên kết user-role hiện có.
     *
     * @param input không dùng
     * @return danh sách UserRoleResult
     */
    @Override
    public List<UserRoleResult> execute(Void input) {
        return repository.findAll().stream()
                .map(ur -> UserRoleResult.builder().userId(ur.getUserId()).roleId(ur.getRoleId()).build())
                .collect(Collectors.toList());
    }
}
