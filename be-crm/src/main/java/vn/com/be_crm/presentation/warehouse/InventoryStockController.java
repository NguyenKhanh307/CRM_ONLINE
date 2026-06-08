package vn.com.be_crm.presentation.warehouse;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.warehouse.command.UpsertInventoryStockUseCase;
import vn.com.be_crm.application.warehouse.dto.*;
import vn.com.be_crm.application.warehouse.query.*;
import vn.com.be_crm.presentation.shared.ApiResponse;
import vn.com.be_crm.presentation.shared.PageResponse;

/**
 * REST controller cho nghiệp vụ quản lý tồn kho.
 */
@RestController
@RequestMapping("/api/inventory-stocks")
public class InventoryStockController {
    private final UpsertInventoryStockUseCase upsertUC;
    private final GetInventoryStockUseCase getUC;
    private final ListInventoryStockUseCase listUC;

    /**
     * @param upsertUC use case tạo/cập nhật @param getUC use case lấy theo ID @param listUC use case danh sách
     */
    public InventoryStockController(UpsertInventoryStockUseCase upsertUC, GetInventoryStockUseCase getUC, ListInventoryStockUseCase listUC) {
        this.upsertUC = upsertUC; this.getUC = getUC; this.listUC = listUC;
    }

    /** Tạo mới hoặc cập nhật tồn kho. @param c command @return 200 */
    @PostMapping
    public ResponseEntity<ApiResponse<InventoryStockResult>> upsert(@Valid @RequestBody UpsertInventoryStockCommand c) {
        return ResponseEntity.ok(ApiResponse.ok(upsertUC.execute(c)));
    }

    /** Danh sách tồn kho có phân trang. @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<InventoryStockResult>>> list(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(listUC.execute(
                PageRequest.builder().page(page).size(size).sortBy(sortBy).sortDir(sortDir).build()))));
    }

    /** Lấy tồn kho theo ID. @param id ID @return 200 */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InventoryStockResult>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(getUC.execute(id)));
    }
}
