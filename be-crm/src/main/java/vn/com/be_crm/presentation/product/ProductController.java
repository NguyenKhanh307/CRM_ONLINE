package vn.com.be_crm.presentation.product;

import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.product.command.*;
import vn.com.be_crm.application.product.dto.*;
import vn.com.be_crm.application.product.query.*;
import vn.com.be_crm.core.dto.delete.DeleteCommand;
import vn.com.be_crm.core.dto.delete.DeletedItemResult;
import vn.com.be_crm.core.dto.import_.ImportBulkResult;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.util.SecurityUtils;
import vn.com.be_crm.core.response.ApiResponse;
import vn.com.be_crm.core.page.PageResponse;

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
    private final ListDeletedProductsUseCase listDeletedUC;
    private final RestoreProductUseCase restoreUC;
    private final PurgeProductUseCase purgeUC;
    private final ImportBulkProductUseCase importBulkUC;

    /**
     * @param createUC use case tạo mới @param updateUC use case cập nhật @param deleteUC use case xóa mềm
     * @param getUC    use case lấy theo ID @param listUC use case lấy danh sách
     * @param listDeletedUC thùng rác @param restoreUC khôi phục @param purgeUC xóa vĩnh viễn @param importBulkUC nhập hàng loạt
     */
    public ProductController(CreateProductUseCase createUC, UpdateProductUseCase updateUC,
                              DeleteProductUseCase deleteUC, GetProductUseCase getUC, ListProductUseCase listUC,
                              ListDeletedProductsUseCase listDeletedUC, RestoreProductUseCase restoreUC, PurgeProductUseCase purgeUC,
                              ImportBulkProductUseCase importBulkUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC;
        this.getUC = getUC; this.listUC = listUC;
        this.listDeletedUC = listDeletedUC; this.restoreUC = restoreUC; this.purgeUC = purgeUC;
        this.importBulkUC = importBulkUC;
    }

    /** Tạo mới hàng hóa. @param cmd JSON body @return 201 */
    @PreAuthorize("hasAuthority('product.create')")
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResult>> create(@Valid @RequestBody CreateProductCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(cmd)));
    }

    /** Lấy danh sách hàng hóa có phân trang. @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductResult>>> list(
            HttpServletRequest req,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy, @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String q, @RequestParam(required = false) String status) {
        Integer fromYear = (Integer) req.getAttribute("dataAccessFromYear");
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(listUC.execute(
                PageRequest.builder().page(page).size(size).sortBy(sortBy).sortDir(sortDir).dataAccessFromYear(fromYear).q(q).status(status).build()))));
    }

    /** Lấy hàng hóa theo ID. @param id ID @return 200 */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResult>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(getUC.execute(id)));
    }

    /** Cập nhật hàng hóa. @param id ID @param cmd JSON body @return 200 */
    @PreAuthorize("hasAuthority('product.edit')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResult>> update(@PathVariable Long id,
                                                              @Valid @RequestBody UpdateProductCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateProductCommand.builder().id(id).name(cmd.getName()).categoryId(cmd.getCategoryId())
                        .type(cmd.getType()).unit(cmd.getUnit())
                        .basePrice(cmd.getBasePrice())
                        .costPrice(cmd.getCostPrice()).vatRate(cmd.getVatRate())
                        .description(cmd.getDescription()).isDiscontinued(cmd.getIsDiscontinued())
                        .isActive(cmd.getIsActive()).build())));
    }

    /** Xóa mềm hàng hóa. @param id ID @param req HTTP request @return 204 */
    @PreAuthorize("hasAuthority('product.delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        deleteUC.execute(new DeleteCommand(id, userId));
        return ResponseEntity.noContent().build();
    }

    /** Lấy danh sách hàng hóa trong thùng rác. @return 200 */
    @GetMapping("/deleted")
    public ResponseEntity<ApiResponse<PageResponse<DeletedItemResult>>> listDeleted(
            HttpServletRequest req,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Long userId = (Long) req.getAttribute("userId");
        boolean isAdmin = SecurityUtils.isAdmin(SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(listDeletedUC.execute(userId, isAdmin,
                PageRequest.builder().page(page).size(size).sortBy("deletedAt").sortDir("desc").build()))));
    }

    /** Khôi phục hàng hóa từ thùng rác. @param id ID @return 200 */
    @PreAuthorize("hasAuthority('product.delete')")
    @PostMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<Void>> restore(@PathVariable Long id) {
        restoreUC.execute(id); return ResponseEntity.ok(ApiResponse.ok(null));
    }

    /** Xóa vĩnh viễn hàng hóa khỏi thùng rác. @param id ID @return 200 */
    @PreAuthorize("hasAuthority('product.delete')")
    @DeleteMapping("/{id}/purge")
    public ResponseEntity<ApiResponse<Void>> purge(@PathVariable Long id) {
        purgeUC.execute(id); return ResponseEntity.ok(ApiResponse.ok(null));
    }

    /** Nhập hàng loạt hàng hóa từ file. @param cmd body @return 200 */
    @PreAuthorize("hasAuthority('product.import')")
    @PostMapping("/import-bulk")
    public ResponseEntity<ApiResponse<ImportBulkResult>> importBulk(@Valid @RequestBody ImportBulkProductCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(importBulkUC.execute(cmd)));
    }
}
