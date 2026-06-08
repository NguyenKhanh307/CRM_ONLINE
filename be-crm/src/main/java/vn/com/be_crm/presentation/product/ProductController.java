package vn.com.be_crm.presentation.product;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.product.command.*;
import vn.com.be_crm.application.product.dto.*;
import vn.com.be_crm.application.product.query.*;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.presentation.shared.ApiResponse;
import vn.com.be_crm.presentation.shared.PageResponse;

/**
 * REST controller cho nghiệp vụ quản lý hàng hóa.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final CreateProductUseCase createUC;
    private final UpdateProductUseCase updateUC;
    private final DeleteProductUseCase deleteUC;
    private final GetProductUseCase getUC;
    private final ListProductUseCase listUC;

    /**
     * @param createUC use case tạo mới @param updateUC use case cập nhật @param deleteUC use case xóa mềm
     * @param getUC    use case lấy theo ID @param listUC use case lấy danh sách
     */
    public ProductController(CreateProductUseCase createUC, UpdateProductUseCase updateUC,
                              DeleteProductUseCase deleteUC, GetProductUseCase getUC, ListProductUseCase listUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC;
        this.getUC = getUC; this.listUC = listUC;
    }

    /** Tạo mới hàng hóa. @param cmd JSON body @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResult>> create(@Valid @RequestBody CreateProductCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(cmd)));
    }

    /** Lấy danh sách hàng hóa có phân trang. @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductResult>>> list(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(listUC.execute(
                PageRequest.builder().page(page).size(size).sortBy(sortBy).sortDir(sortDir).build()))));
    }

    /** Lấy hàng hóa theo ID. @param id ID @return 200 */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResult>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(getUC.execute(id)));
    }

    /** Cập nhật hàng hóa. @param id ID @param cmd JSON body @return 200 */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResult>> update(@PathVariable Long id,
                                                              @Valid @RequestBody UpdateProductCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateProductCommand.builder().id(id).name(cmd.getName()).categoryId(cmd.getCategoryId())
                        .type(cmd.getType()).unit(cmd.getUnit()).basePrice(cmd.getBasePrice())
                        .costPrice(cmd.getCostPrice()).vatRate(cmd.getVatRate()).barcode(cmd.getBarcode())
                        .description(cmd.getDescription()).isDiscontinued(cmd.getIsDiscontinued())
                        .isActive(cmd.getIsActive()).build())));
    }

    /** Xóa mềm hàng hóa. @param id ID @return 204 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUC.execute(id); return ResponseEntity.noContent().build();
    }
}
