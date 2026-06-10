package vn.com.be_crm.presentation.customer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.customer.command.*;
import vn.com.be_crm.application.customer.dto.*;
import vn.com.be_crm.application.customer.query.*;
import vn.com.be_crm.application.shared.dto.DeleteCommand;
import vn.com.be_crm.application.shared.dto.DeletedItemResult;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.infrastructure.shared.util.SecurityUtils;
import vn.com.be_crm.presentation.shared.ApiResponse;
import vn.com.be_crm.presentation.shared.PageResponse;

/**
 * REST controller cho nghiệp vụ quản lý khách hàng.
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CreateCustomerUseCase createUC;
    private final UpdateCustomerUseCase updateUC;
    private final DeleteCustomerUseCase deleteUC;
    private final GetCustomerUseCase getUC;
    private final ListCustomerUseCase listUC;
    private final ListDeletedCustomersUseCase listDeletedUC;
    private final RestoreCustomerUseCase restoreUC;
    private final PurgeCustomerUseCase purgeUC;

    /**
     * @param createUC use case tạo mới @param updateUC use case cập nhật @param deleteUC use case xóa mềm
     * @param getUC    use case lấy theo ID @param listUC use case lấy danh sách
     * @param listDeletedUC thùng rác @param restoreUC khôi phục @param purgeUC xóa vĩnh viễn
     */
    public CustomerController(CreateCustomerUseCase createUC, UpdateCustomerUseCase updateUC,
                               DeleteCustomerUseCase deleteUC, GetCustomerUseCase getUC, ListCustomerUseCase listUC,
                               ListDeletedCustomersUseCase listDeletedUC, RestoreCustomerUseCase restoreUC, PurgeCustomerUseCase purgeUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC;
        this.getUC = getUC; this.listUC = listUC;
        this.listDeletedUC = listDeletedUC; this.restoreUC = restoreUC; this.purgeUC = purgeUC;
    }

    /** Tạo mới khách hàng. @param cmd JSON body @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResult>> create(@Valid @RequestBody CreateCustomerCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(cmd)));
    }

    /** Lấy danh sách khách hàng có phân trang. @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CustomerResult>>> list(
            HttpServletRequest req,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String sortDir) {
        Integer fromYear = (Integer) req.getAttribute("dataAccessFromYear");
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(listUC.execute(
                PageRequest.builder().page(page).size(size).sortBy(sortBy).sortDir(sortDir).dataAccessFromYear(fromYear).build()))));
    }

    /** Lấy khách hàng theo ID. @param id ID @return 200 */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResult>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(getUC.execute(id)));
    }

    /** Cập nhật khách hàng. @param id ID @param cmd JSON body @return 200 */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResult>> update(@PathVariable Long id,
                                                               @Valid @RequestBody UpdateCustomerCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateCustomerCommand.builder().id(id).name(cmd.getName()).type(cmd.getType())
                        .taxCode(cmd.getTaxCode()).phone(cmd.getPhone()).email(cmd.getEmail())
                        .address(cmd.getAddress()).source(cmd.getSource()).status(cmd.getStatus())
                        .ownerId(cmd.getOwnerId()).unitId(cmd.getUnitId()).build())));
    }

    /** Xóa mềm khách hàng. @param id ID @param req HTTP request @return 204 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        deleteUC.execute(new DeleteCommand(id, userId));
        return ResponseEntity.noContent().build();
    }

    /** Lấy danh sách khách hàng trong thùng rác. @return 200 */
    @GetMapping("/deleted")
    public ResponseEntity<ApiResponse<PageResponse<DeletedItemResult>>> listDeleted(
            HttpServletRequest req,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Long userId = (Long) req.getAttribute("userId");
        boolean isAdmin = SecurityUtils.isAdmin(SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(listDeletedUC.execute(userId, isAdmin,
                PageRequest.builder().page(page).size(size).sortBy("deletedAt").sortDir("desc").build()))));
    }

    /** Khôi phục khách hàng từ thùng rác. @param id ID @return 200 */
    @PostMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<Void>> restore(@PathVariable Long id) {
        restoreUC.execute(id); return ResponseEntity.ok(ApiResponse.ok(null));
    }

    /** Xóa vĩnh viễn khách hàng khỏi thùng rác. @param id ID @return 200 */
    @DeleteMapping("/{id}/purge")
    public ResponseEntity<ApiResponse<Void>> purge(@PathVariable Long id) {
        purgeUC.execute(id); return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
