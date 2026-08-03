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

import java.util.List;

/**
 * REST controller cho nghiệp vụ quản lý vai trò.
 */
@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final CreateRoleUseCase createUseCase;
    private final UpdateRoleUseCase updateUseCase;
    private final DeleteRoleUseCase deleteUseCase;
    private final GetRoleUseCase getUseCase;
    private final ListRoleUseCase listUseCase;
    private final AssignRolePermissionUseCase assignPermUseCase;
    private final RevokeRolePermissionUseCase revokePermUseCase;
    private final ListRolePermissionsUseCase listRolePermissionsUseCase;
    private final ListRoleMembersUseCase listRoleMembersUseCase;
    private final ListUserRoleAssignmentsUseCase listUserRoleAssignmentsUseCase;

    /**
     * @param createUseCase              use case tạo mới
     * @param updateUseCase              use case cập nhật
     * @param deleteUseCase              use case xóa
     * @param getUseCase                 use case lấy theo ID
     * @param listUseCase                use case lấy danh sách
     * @param assignPermUseCase          use case gán quyền
     * @param revokePermUseCase          use case thu hồi quyền
     * @param listRolePermissionsUseCase use case lấy danh sách quyền của vai trò
     * @param listRoleMembersUseCase     use case lấy danh sách thành viên của vai trò
     * @param listUserRoleAssignmentsUseCase use case lấy toàn bộ liên kết user-role
     */
    public RoleController(CreateRoleUseCase createUseCase, UpdateRoleUseCase updateUseCase,
                           DeleteRoleUseCase deleteUseCase, GetRoleUseCase getUseCase,
                           ListRoleUseCase listUseCase, AssignRolePermissionUseCase assignPermUseCase,
                           RevokeRolePermissionUseCase revokePermUseCase,
                           ListRolePermissionsUseCase listRolePermissionsUseCase,
                           ListRoleMembersUseCase listRoleMembersUseCase,
                           ListUserRoleAssignmentsUseCase listUserRoleAssignmentsUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.deleteUseCase = deleteUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
        this.assignPermUseCase = assignPermUseCase;
        this.revokePermUseCase = revokePermUseCase;
        this.listRolePermissionsUseCase = listRolePermissionsUseCase;
        this.listRoleMembersUseCase = listRoleMembersUseCase;
        this.listUserRoleAssignmentsUseCase = listUserRoleAssignmentsUseCase;
    }

    /**
     * Tạo mới vai trò.
     *
     * @param request JSON body tạo mới
     * @return 201 Created với RoleResult
     */
    @PostMapping
    public ResponseEntity<ApiResponse<RoleResult>> create(@Valid @RequestBody CreateRoleRequest request) {
        CreateRoleCommand cmd = CreateRoleCommand.builder()
                .code(request.getCode()).name(request.getName())
                .description(request.getDescription()).isSystem(request.getIsSystem()).build();
        return ResponseEntity.status(201).body(ApiResponse.created(createUseCase.execute(cmd)));
    }

    /**
     * Lấy danh sách vai trò có phân trang.
     *
     * @param page    số trang
     * @param size    số bản ghi mỗi trang
     * @param sortBy  field sắp xếp
     * @param sortDir chiều sắp xếp
     * @return 200 OK với PageResponse
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<RoleResult>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        PageResult<RoleResult> result = listUseCase.execute(
                PageRequest.builder().page(page).size(size).sortBy(sortBy).sortDir(sortDir).build());
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(result)));
    }

    /**
     * Lấy thông tin vai trò theo ID.
     *
     * @param id ID vai trò
     * @return 200 OK với RoleResult
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResult>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(getUseCase.execute(id)));
    }

    /**
     * Cập nhật vai trò.
     *
     * @param id      ID vai trò cần cập nhật
     * @param request JSON body cập nhật
     * @return 200 OK với RoleResult đã cập nhật
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResult>> update(@PathVariable Long id,
                                                           @Valid @RequestBody UpdateRoleRequest request) {
        UpdateRoleCommand cmd = UpdateRoleCommand.builder()
                .id(id).name(request.getName()).description(request.getDescription()).build();
        return ResponseEntity.ok(ApiResponse.ok(updateUseCase.execute(cmd)));
    }

    /**
     * Xóa vai trò (không cho phép xóa system role).
     *
     * @param id ID vai trò cần xóa
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Gán quyền cho vai trò.
     *
     * @param id      ID vai trò
     * @param request JSON body chứa permissionId
     * @return 200 OK
     */
    @PostMapping("/{id}/permissions")
    public ResponseEntity<ApiResponse<Void>> assignPermission(@PathVariable Long id,
                                                               @Valid @RequestBody AssignRolePermissionRequest request) {
        assignPermUseCase.execute(AssignRolePermissionCommand.builder()
                .roleId(id).permissionId(request.getPermissionId()).build());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    /**
     * Thu hồi quyền khỏi vai trò.
     *
     * @param id           ID vai trò
     * @param permissionId ID quyền cần thu hồi
     * @return 204 No Content
     */
    @DeleteMapping("/{id}/permissions/{permissionId}")
    public ResponseEntity<Void> revokePermission(@PathVariable Long id, @PathVariable Long permissionId) {
        revokePermUseCase.execute(AssignRolePermissionCommand.builder()
                .roleId(id).permissionId(permissionId).build());
        return ResponseEntity.noContent().build();
    }

    /**
     * Lấy danh sách quyền đã gán cho vai trò.
     *
     * @param id ID vai trò
     * @return 200 OK với danh sách PermissionResult
     */
    @GetMapping("/{id}/permissions")
    public ResponseEntity<ApiResponse<List<PermissionResult>>> listPermissions(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(listRolePermissionsUseCase.execute(id)));
    }

    /**
     * Lấy danh sách thành viên (user) thuộc vai trò.
     *
     * @param id ID vai trò
     * @return 200 OK với danh sách UserResult
     */
    @GetMapping("/{id}/members")
    public ResponseEntity<ApiResponse<List<UserResult>>> listMembers(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(listRoleMembersUseCase.execute(id)));
    }

    /**
     * Lấy toàn bộ liên kết user-role hiện có — FE dùng để biết người dùng nào đã thuộc một nhóm
     * bất kỳ (mỗi người chỉ thuộc một nhóm).
     *
     * @return 200 OK với danh sách UserRoleResult
     */
    @GetMapping("/user-assignments")
    public ResponseEntity<ApiResponse<List<UserRoleResult>>> listUserAssignments() {
        return ResponseEntity.ok(ApiResponse.ok(listUserRoleAssignmentsUseCase.execute(null)));
    }
}
