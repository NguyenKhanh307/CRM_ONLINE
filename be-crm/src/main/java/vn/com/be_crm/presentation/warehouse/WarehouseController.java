package vn.com.be_crm.presentation.warehouse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.shared.dto.ImportBulkResult;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.warehouse.command.*;
import vn.com.be_crm.application.warehouse.dto.*;
import vn.com.be_crm.application.warehouse.query.*;
import vn.com.be_crm.presentation.shared.ApiResponse;
import vn.com.be_crm.presentation.shared.PageResponse;

/**
 * REST controller cho nghiệp vụ quản lý kho hàng.
 */
@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {
    private final CreateWarehouseUseCase createUC;
    private final UpdateWarehouseUseCase updateUC;
    private final DeleteWarehouseUseCase deleteUC;
    private final GetWarehouseUseCase getUC;
    private final ListWarehouseUseCase listUC;
    private final AssignWarehousePermissionUseCase assignPermUC;
    private final RevokeWarehousePermissionUseCase revokePermUC;
    private final ImportBulkWarehouseUseCase importBulkUC;

    /**
     * @param createUC   use case tạo mới @param updateUC use case cập nhật @param deleteUC use case xóa
     * @param getUC      use case lấy theo ID @param listUC use case lấy danh sách
     * @param assignPermUC use case gán quyền @param revokePermUC use case thu hồi quyền @param importBulkUC nhập hàng loạt
     */
    public WarehouseController(CreateWarehouseUseCase createUC, UpdateWarehouseUseCase updateUC,
                                DeleteWarehouseUseCase deleteUC, GetWarehouseUseCase getUC, ListWarehouseUseCase listUC,
                                AssignWarehousePermissionUseCase assignPermUC, RevokeWarehousePermissionUseCase revokePermUC,
                                ImportBulkWarehouseUseCase importBulkUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC;
        this.getUC = getUC; this.listUC = listUC; this.assignPermUC = assignPermUC; this.revokePermUC = revokePermUC;
        this.importBulkUC = importBulkUC;
    }

    /** Tạo mới kho. @param c command @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<WarehouseResult>> create(@Valid @RequestBody CreateWarehouseCommand c) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(c)));
    }

    /** Danh sách kho có phân trang. @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<WarehouseResult>>> list(
            HttpServletRequest req,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String sortDir) {
        Integer fromYear = (Integer) req.getAttribute("dataAccessFromYear");
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(listUC.execute(
                PageRequest.builder().page(page).size(size).sortBy(sortBy).sortDir(sortDir).dataAccessFromYear(fromYear).build()))));
    }

    /** Lấy kho theo ID. @param id ID @return 200 */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WarehouseResult>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(getUC.execute(id)));
    }

    /** Cập nhật kho. @param id ID @param c command @return 200 */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WarehouseResult>> update(@PathVariable Long id, @Valid @RequestBody UpdateWarehouseCommand c) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateWarehouseCommand.builder().id(id).name(c.getName()).address(c.getAddress())
                        .managerId(c.getManagerId()).isActive(c.getIsActive()).build())));
    }

    /** Xóa kho. @param id ID @return 204 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUC.execute(id); return ResponseEntity.noContent().build();
    }

    /** Gán quyền kho cho user. @param id warehouseId @param c command @return 200 */
    @PostMapping("/{id}/permissions")
    public ResponseEntity<ApiResponse<Void>> assignPermission(@PathVariable Long id,
                                                               @Valid @RequestBody AssignWarehousePermissionCommand c) {
        assignPermUC.execute(AssignWarehousePermissionCommand.builder()
                .warehouseId(id).userId(c.getUserId()).permission(c.getPermission()).build());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    /** Thu hồi quyền kho. @param id warehouseId @param userId user ID @return 204 */
    @DeleteMapping("/{id}/permissions/{userId}")
    public ResponseEntity<Void> revokePermission(@PathVariable Long id, @PathVariable Long userId) {
        revokePermUC.execute(AssignWarehousePermissionCommand.builder().warehouseId(id).userId(userId).build());
        return ResponseEntity.noContent().build();
    }

    /** Nhập hàng loạt kho từ file. @param cmd body @return 200 */
    @PostMapping("/import-bulk")
    public ResponseEntity<ApiResponse<ImportBulkResult>> importBulk(@Valid @RequestBody ImportBulkWarehouseCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(importBulkUC.execute(cmd)));
    }
}
