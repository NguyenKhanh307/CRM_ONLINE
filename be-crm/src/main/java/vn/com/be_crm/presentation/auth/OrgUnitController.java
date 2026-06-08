package vn.com.be_crm.presentation.auth;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.auth.command.*;
import vn.com.be_crm.application.auth.dto.*;
import vn.com.be_crm.application.auth.query.*;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.presentation.auth.request.CreateOrgUnitRequest;
import vn.com.be_crm.presentation.auth.request.UpdateOrgUnitRequest;
import vn.com.be_crm.presentation.shared.ApiResponse;
import vn.com.be_crm.presentation.shared.PageResponse;

/**
 * REST controller cho nghiệp vụ quản lý đơn vị tổ chức.
 */
@RestController
@RequestMapping("/api/org-units")
public class OrgUnitController {

    private final CreateOrgUnitUseCase createUseCase;
    private final UpdateOrgUnitUseCase updateUseCase;
    private final DeleteOrgUnitUseCase deleteUseCase;
    private final GetOrgUnitUseCase getUseCase;
    private final ListOrgUnitUseCase listUseCase;

    /**
     * @param createUseCase use case tạo mới
     * @param updateUseCase use case cập nhật
     * @param deleteUseCase use case xóa
     * @param getUseCase    use case lấy theo ID
     * @param listUseCase   use case lấy danh sách
     */
    public OrgUnitController(CreateOrgUnitUseCase createUseCase,
                              UpdateOrgUnitUseCase updateUseCase,
                              DeleteOrgUnitUseCase deleteUseCase,
                              GetOrgUnitUseCase getUseCase,
                              ListOrgUnitUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.deleteUseCase = deleteUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    /**
     * Tạo mới đơn vị tổ chức.
     *
     * @param request JSON body tạo mới
     * @return 201 Created với OrgUnitResult
     */
    @PostMapping
    public ResponseEntity<ApiResponse<OrgUnitResult>> create(@Valid @RequestBody CreateOrgUnitRequest request) {
        CreateOrgUnitCommand cmd = CreateOrgUnitCommand.builder()
                .code(request.getCode()).name(request.getName())
                .parentId(request.getParentId()).level(request.getLevel())
                .path(request.getPath()).sortOrder(request.getSortOrder())
                .isActive(request.getIsActive()).build();
        return ResponseEntity.status(201).body(ApiResponse.created(createUseCase.execute(cmd)));
    }

    /**
     * Lấy danh sách đơn vị tổ chức có phân trang.
     *
     * @param page    số trang (0-based)
     * @param size    số bản ghi mỗi trang
     * @param sortBy  tên field sắp xếp
     * @param sortDir chiều sắp xếp (asc/desc)
     * @return 200 OK với PageResponse
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OrgUnitResult>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        PageRequest req = PageRequest.builder().page(page).size(size).sortBy(sortBy).sortDir(sortDir).build();
        PageResult<OrgUnitResult> result = listUseCase.execute(req);
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(result)));
    }

    /**
     * Lấy thông tin đơn vị tổ chức theo ID.
     *
     * @param id ID đơn vị
     * @return 200 OK với OrgUnitResult
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrgUnitResult>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(getUseCase.execute(id)));
    }

    /**
     * Cập nhật đơn vị tổ chức.
     *
     * @param id      ID đơn vị cần cập nhật
     * @param request JSON body cập nhật
     * @return 200 OK với OrgUnitResult đã cập nhật
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrgUnitResult>> update(@PathVariable Long id,
                                                              @Valid @RequestBody UpdateOrgUnitRequest request) {
        UpdateOrgUnitCommand cmd = UpdateOrgUnitCommand.builder()
                .id(id).name(request.getName()).parentId(request.getParentId())
                .level(request.getLevel()).path(request.getPath())
                .sortOrder(request.getSortOrder()).isActive(request.getIsActive()).build();
        return ResponseEntity.ok(ApiResponse.ok(updateUseCase.execute(cmd)));
    }

    /**
     * Xóa đơn vị tổ chức.
     *
     * @param id ID đơn vị cần xóa
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
