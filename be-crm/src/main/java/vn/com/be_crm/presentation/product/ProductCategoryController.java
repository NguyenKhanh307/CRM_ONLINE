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
 * REST controller cho nghiệp vụ quản lý danh mục hàng hóa.
 */
@RestController
@RequestMapping("/api/product-categories")
public class ProductCategoryController {
    private final CreateProductCategoryUseCase createUC;
    private final UpdateProductCategoryUseCase updateUC;
    private final DeleteProductCategoryUseCase deleteUC;
    private final GetProductCategoryUseCase getUC;
    private final ListProductCategoryUseCase listUC;

    /**
     * @param createUC use case tạo mới @param updateUC use case cập nhật @param deleteUC use case xóa
     * @param getUC    use case lấy theo ID @param listUC use case lấy danh sách
     */
    public ProductCategoryController(CreateProductCategoryUseCase createUC, UpdateProductCategoryUseCase updateUC,
                                      DeleteProductCategoryUseCase deleteUC, GetProductCategoryUseCase getUC,
                                      ListProductCategoryUseCase listUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC;
        this.getUC = getUC; this.listUC = listUC;
    }

    /** Tạo mới danh mục. @param cmd JSON body @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<ProductCategoryResult>> create(@Valid @RequestBody CreateProductCategoryCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(cmd)));
    }

    /** Lấy danh sách danh mục có phân trang. @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductCategoryResult>>> list(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(listUC.execute(
                PageRequest.builder().page(page).size(size).sortBy(sortBy).sortDir(sortDir).build()))));
    }

    /** Lấy danh mục theo ID. @param id ID @return 200 */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductCategoryResult>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(getUC.execute(id)));
    }

    /** Cập nhật danh mục. @param id ID @param cmd JSON body @return 200 */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductCategoryResult>> update(@PathVariable Long id,
                                                                      @Valid @RequestBody UpdateProductCategoryCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateProductCategoryCommand.builder().id(id).name(cmd.getName())
                        .parentId(cmd.getParentId()).sortOrder(cmd.getSortOrder()).isActive(cmd.getIsActive()).build())));
    }

    /** Xóa danh mục. @param id ID @return 204 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUC.execute(id); return ResponseEntity.noContent().build();
    }
}
