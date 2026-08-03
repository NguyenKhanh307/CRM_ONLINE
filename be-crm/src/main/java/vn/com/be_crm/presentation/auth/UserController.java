package vn.com.be_crm.presentation.auth;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.auth.command.*;
import vn.com.be_crm.application.auth.dto.*;
import vn.com.be_crm.application.auth.query.*;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.page.PageResult;
import vn.com.be_crm.presentation.auth.request.*;
import vn.com.be_crm.core.response.ApiResponse;
import vn.com.be_crm.core.page.PageResponse;

/**
 * REST controller cho nghiệp vụ quản lý người dùng.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final CreateUserUseCase createUseCase;
    private final UpdateUserUseCase updateUseCase;
    private final DeleteUserUseCase deleteUseCase;
    private final GetUserUseCase getUseCase;
    private final ListUserUseCase listUseCase;
    private final AssignUserRoleUseCase assignRoleUseCase;
    private final RevokeUserRoleUseCase revokeRoleUseCase;

    /**
     * @param createUseCase    use case tạo mới
     * @param updateUseCase    use case cập nhật
     * @param deleteUseCase    use case xóa mềm
     * @param getUseCase       use case lấy theo ID
     * @param listUseCase      use case lấy danh sách
     * @param assignRoleUseCase use case gán vai trò
     * @param revokeRoleUseCase use case thu hồi vai trò
     */
    public UserController(CreateUserUseCase createUseCase, UpdateUserUseCase updateUseCase,
                           DeleteUserUseCase deleteUseCase, GetUserUseCase getUseCase,
                           ListUserUseCase listUseCase, AssignUserRoleUseCase assignRoleUseCase,
                           RevokeUserRoleUseCase revokeRoleUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.deleteUseCase = deleteUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
        this.assignRoleUseCase = assignRoleUseCase;
        this.revokeRoleUseCase = revokeRoleUseCase;
    }

    /**
     * Tạo mới người dùng.
     *
     * @param request JSON body tạo mới
     * @return 201 Created với UserResult
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserResult>> create(@Valid @RequestBody CreateUserRequest request) {
        CreateUserCommand cmd = CreateUserCommand.builder()
                .email(request.getEmail()).passwordHash(request.getPasswordHash())
                .fullName(request.getFullName()).phone(request.getPhone())
                .avatarUrl(request.getAvatarUrl())
                .status(request.getStatus()).build();
        return ResponseEntity.status(201).body(ApiResponse.created(createUseCase.execute(cmd)));
    }

    /**
     * Lấy danh sách người dùng có phân trang.
     *
     * @param page    số trang
     * @param size    số bản ghi mỗi trang
     * @param sortBy  field sắp xếp
     * @param sortDir chiều sắp xếp
     * @return 200 OK với PageResponse
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<UserResult>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String status) {
        PageResult<UserResult> result = listUseCase.execute(
                PageRequest.builder().page(page).size(size).sortBy(sortBy).sortDir(sortDir).status(status).build());
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(result)));
    }

    /**
     * Lấy thông tin người dùng theo ID.
     *
     * @param id ID người dùng
     * @return 200 OK với UserResult
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResult>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(getUseCase.execute(id)));
    }

    /**
     * Cập nhật người dùng.
     *
     * @param id      ID người dùng cần cập nhật
     * @param request JSON body cập nhật
     * @return 200 OK với UserResult đã cập nhật
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResult>> update(@PathVariable Long id,
                                                           @Valid @RequestBody UpdateUserRequest request) {
        UpdateUserCommand cmd = UpdateUserCommand.builder()
                .id(id).fullName(request.getFullName()).phone(request.getPhone())
                .avatarUrl(request.getAvatarUrl())
                .status(request.getStatus()).dataAccessFromYear(request.getDataAccessFromYear()).build();
        return ResponseEntity.ok(ApiResponse.ok(updateUseCase.execute(cmd)));
    }

    /**
     * Thu hồi tài khoản nhân viên — chuyển status sang locked.
     *
     * @param id ID người dùng cần thu hồi
     * @return 200 OK với UserResult đã cập nhật
     */
    @PutMapping("/{id}/revoke")
    public ResponseEntity<ApiResponse<UserResult>> revoke(@PathVariable Long id) {
        UpdateUserCommand cmd = UpdateUserCommand.builder()
                .id(id).status(vn.com.be_crm.domain.auth.enums.UserStatus.locked).build();
        return ResponseEntity.ok(ApiResponse.ok(updateUseCase.execute(cmd)));
    }

    /**
     * Kích hoạt lại tài khoản nhân viên bị thu hồi — chuyển status sang active.
     *
     * @param id ID người dùng cần kích hoạt lại
     * @return 200 OK với UserResult đã cập nhật
     */
    @PutMapping("/{id}/reactivate")
    public ResponseEntity<ApiResponse<UserResult>> reactivate(@PathVariable Long id) {
        UpdateUserCommand cmd = UpdateUserCommand.builder()
                .id(id).status(vn.com.be_crm.domain.auth.enums.UserStatus.active).build();
        return ResponseEntity.ok(ApiResponse.ok(updateUseCase.execute(cmd)));
    }

    /**
     * Xóa mềm người dùng.
     *
     * @param id ID người dùng cần xóa
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Gán vai trò cho người dùng.
     *
     * @param id      ID người dùng
     * @param request JSON body chứa roleId
     * @return 200 OK
     */
    @PostMapping("/{id}/roles")
    public ResponseEntity<ApiResponse<Void>> assignRole(@PathVariable Long id,
                                                         @Valid @RequestBody AssignUserRoleRequest request) {
        assignRoleUseCase.execute(AssignUserRoleCommand.builder().userId(id).roleId(request.getRoleId()).build());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    /**
     * Thu hồi vai trò khỏi người dùng.
     *
     * @param id     ID người dùng
     * @param roleId ID vai trò cần thu hồi
     * @return 204 No Content
     */
    @DeleteMapping("/{id}/roles/{roleId}")
    public ResponseEntity<Void> revokeRole(@PathVariable Long id, @PathVariable Long roleId) {
        revokeRoleUseCase.execute(AssignUserRoleCommand.builder().userId(id).roleId(roleId).build());
        return ResponseEntity.noContent().build();
    }
}
