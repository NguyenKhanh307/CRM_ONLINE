package vn.com.be_crm.presentation.customer;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.customer.command.*;
import vn.com.be_crm.application.customer.dto.*;
import vn.com.be_crm.application.customer.query.ListInventoryCheckItemUseCase;
import vn.com.be_crm.presentation.shared.ApiResponse;

import java.util.List;

/**
 * REST controller cho nghiệp vụ quản lý dòng kiểm kho.
 */
@RestController
@RequestMapping("/api/inventory-checks/{checkId}/items")
public class InventoryCheckItemController {
    private final CreateInventoryCheckItemUseCase createUC;
    private final UpdateInventoryCheckItemUseCase updateUC;
    private final DeleteInventoryCheckItemUseCase deleteUC;
    private final ListInventoryCheckItemUseCase listUC;

    /**
     * @param createUC use case tạo mới @param updateUC use case cập nhật
     * @param deleteUC use case xóa @param listUC use case lấy danh sách
     */
    public InventoryCheckItemController(CreateInventoryCheckItemUseCase createUC,
                                         UpdateInventoryCheckItemUseCase updateUC,
                                         DeleteInventoryCheckItemUseCase deleteUC,
                                         ListInventoryCheckItemUseCase listUC) {
        this.createUC = createUC; this.updateUC = updateUC;
        this.deleteUC = deleteUC; this.listUC = listUC;
    }

    /** Tạo mới dòng kiểm kho. @param checkId ID phiếu @param cmd JSON body @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<InventoryCheckItemResult>> create(@PathVariable Long checkId,
                                                                         @Valid @RequestBody CreateInventoryCheckItemCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(
                CreateInventoryCheckItemCommand.builder().checkId(checkId).productId(cmd.getProductId())
                        .quantity(cmd.getQuantity()).note(cmd.getNote()).build())));
    }

    /** Lấy danh sách dòng kiểm kho theo checkId. @param checkId ID phiếu @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<List<InventoryCheckItemResult>>> list(@PathVariable Long checkId) {
        return ResponseEntity.ok(ApiResponse.ok(listUC.execute(checkId)));
    }

    /** Cập nhật dòng kiểm kho. @param id ID @param cmd JSON body @return 200 */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InventoryCheckItemResult>> update(@PathVariable Long checkId,
                                                                         @PathVariable Long id,
                                                                         @Valid @RequestBody UpdateInventoryCheckItemCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateInventoryCheckItemCommand.builder().id(id).productId(cmd.getProductId())
                        .quantity(cmd.getQuantity()).note(cmd.getNote()).build())));
    }

    /** Xóa dòng kiểm kho. @param id ID @return 204 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long checkId, @PathVariable Long id) {
        deleteUC.execute(id); return ResponseEntity.noContent().build();
    }
}
