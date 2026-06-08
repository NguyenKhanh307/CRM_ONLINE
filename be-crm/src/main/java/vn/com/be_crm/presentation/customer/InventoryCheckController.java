package vn.com.be_crm.presentation.customer;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.customer.command.*;
import vn.com.be_crm.application.customer.dto.*;
import vn.com.be_crm.application.customer.query.*;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.presentation.shared.ApiResponse;
import vn.com.be_crm.presentation.shared.PageResponse;

/**
 * REST controller cho nghiệp vụ quản lý phiếu kiểm kho.
 */
@RestController
@RequestMapping("/api/inventory-checks")
public class InventoryCheckController {
    private final CreateInventoryCheckUseCase createUC;
    private final UpdateInventoryCheckUseCase updateUC;
    private final DeleteInventoryCheckUseCase deleteUC;
    private final GetInventoryCheckUseCase getUC;
    private final ListInventoryCheckUseCase listUC;

    /**
     * @param createUC use case tạo mới @param updateUC use case cập nhật @param deleteUC use case xóa
     * @param getUC    use case lấy theo ID @param listUC use case lấy danh sách
     */
    public InventoryCheckController(CreateInventoryCheckUseCase createUC, UpdateInventoryCheckUseCase updateUC,
                                     DeleteInventoryCheckUseCase deleteUC, GetInventoryCheckUseCase getUC,
                                     ListInventoryCheckUseCase listUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC;
        this.getUC = getUC; this.listUC = listUC;
    }

    /** Tạo mới phiếu kiểm kho. @param cmd JSON body @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<InventoryCheckResult>> create(@Valid @RequestBody CreateInventoryCheckCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(cmd)));
    }

    /** Lấy danh sách phiếu kiểm kho. @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<InventoryCheckResult>>> list(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(listUC.execute(
                PageRequest.builder().page(page).size(size).sortBy(sortBy).sortDir(sortDir).build()))));
    }

    /** Lấy phiếu kiểm kho theo ID. @param id ID @return 200 */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InventoryCheckResult>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(getUC.execute(id)));
    }

    /** Cập nhật phiếu kiểm kho. @param id ID @param cmd JSON body @return 200 */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InventoryCheckResult>> update(@PathVariable Long id,
                                                                     @Valid @RequestBody UpdateInventoryCheckCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateInventoryCheckCommand.builder().id(id).checkedBy(cmd.getCheckedBy())
                        .checkDate(cmd.getCheckDate()).status(cmd.getStatus()).note(cmd.getNote()).build())));
    }

    /** Xóa phiếu kiểm kho. @param id ID @return 204 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUC.execute(id); return ResponseEntity.noContent().build();
    }
}
