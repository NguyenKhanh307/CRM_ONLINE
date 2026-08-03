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
 * REST controller cho nghiệp vụ quản lý quyền hạn.
 */
@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    private final CreatePermissionUseCase createUseCase;
    private final UpdatePermissionUseCase updateUseCase;
    private final DeletePermissionUseCase deleteUseCase;
    private final GetPermissionUseCase getUseCase;
    private final ListPermissionUseCase listUseCase;

    /**
     * @param createUseCase use case tạo mới
     * @param updateUseCase use case cập nhật
     * @param deleteUseCase use case xóa
     * @param getUseCase    use case lấy theo ID
     * @param listUseCase   use case lấy danh sách
     */
    public PermissionController(CreatePermissionUseCase createUseCase, UpdatePermissionUseCase updateUseCase,
                                 DeletePermissionUseCase deleteUseCase, GetPermissionUseCase getUseCase,
                                 ListPermissionUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.deleteUseCase = deleteUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    /**
     * Tạo mới quyền hạn.
     *
     * @param request JSON body tạo mới
     * @return 201 Created với PermissionResult
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PermissionResult>> create(@Valid @RequestBody CreatePermissionRequest request) {
        CreatePermissionCommand cmd = CreatePermissionCommand.builder()
                .code(request.getCode()).name(request.getName())
                .module(request.getModule()).description(request.getDescription()).build();
        return ResponseEntity.status(201).body(ApiResponse.created(createUseCase.execute(cmd)));
    }

    /**
     * Lấy danh sách quyền hạn có phân trang.
     *
     * @param page    số trang
     * @param size    số bản ghi mỗi trang
     * @param sortBy  field sắp xếp
     * @param sortDir chiều sắp xếp
     * @return 200 OK với PageResponse
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PermissionResult>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        PageResult<PermissionResult> result = listUseCase.execute(
                PageRequest.builder().page(page).size(size).sortBy(sortBy).sortDir(sortDir).build());
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(result)));
    }

    /**
     * Lấy thông tin quyền hạn theo ID.
     *
     * @param id ID quyền
     * @return 200 OK với PermissionResult
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PermissionResult>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(getUseCase.execute(id)));
    }

    /**
     * Cập nhật quyền hạn.
     *
     * @param id      ID quyền cần cập nhật
     * @param request JSON body cập nhật
     * @return 200 OK với PermissionResult đã cập nhật
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PermissionResult>> update(@PathVariable Long id,
                                                                 @Valid @RequestBody UpdatePermissionRequest request) {
        UpdatePermissionCommand cmd = UpdatePermissionCommand.builder()
                .id(id).name(request.getName()).module(request.getModule())
                .description(request.getDescription()).build();
        return ResponseEntity.ok(ApiResponse.ok(updateUseCase.execute(cmd)));
    }

    /**
     * Xóa quyền hạn.
     *
     * @param id ID quyền cần xóa
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
