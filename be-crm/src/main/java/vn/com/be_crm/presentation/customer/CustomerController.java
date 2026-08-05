package vn.com.be_crm.presentation.customer;

import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.customer.command.*;
import vn.com.be_crm.application.customer.dto.*;
import vn.com.be_crm.application.customer.query.*;
import vn.com.be_crm.core.dto.delete.DeleteCommand;
import vn.com.be_crm.core.dto.delete.DeletedItemResult;
import vn.com.be_crm.core.dto.handover.HandoverBulkCommand;
import vn.com.be_crm.core.dto.import_.ImportBulkResult;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.util.SecurityUtils;
import vn.com.be_crm.core.response.ApiResponse;
import vn.com.be_crm.core.dto.handover.HandoverBulkRequest;
import vn.com.be_crm.core.page.PageResponse;

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
    private final ImportBulkCustomerUseCase importBulkUC;
    private final vn.com.be_crm.application.customer.command.HandoverBulkCustomerUseCase handoverBulkUC;
    private final vn.com.be_crm.application.customer.command.CustomerWorkflowUseCase workflowUC;

    /**
     * @param createUC use case tạo mới @param updateUC use case cập nhật @param deleteUC use case xóa mềm
     * @param getUC    use case lấy theo ID @param listUC use case lấy danh sách
     * @param listDeletedUC thùng rác @param restoreUC khôi phục @param purgeUC xóa vĩnh viễn @param importBulkUC nhập hàng loạt
     * @param handoverBulkUC bàn giao hàng loạt @param workflowUC luồng trạng thái khách hàng
     */
    public CustomerController(CreateCustomerUseCase createUC, UpdateCustomerUseCase updateUC,
                               DeleteCustomerUseCase deleteUC, GetCustomerUseCase getUC, ListCustomerUseCase listUC,
                               ListDeletedCustomersUseCase listDeletedUC, RestoreCustomerUseCase restoreUC, PurgeCustomerUseCase purgeUC,
                               ImportBulkCustomerUseCase importBulkUC,
                               vn.com.be_crm.application.customer.command.HandoverBulkCustomerUseCase handoverBulkUC,
                               vn.com.be_crm.application.customer.command.CustomerWorkflowUseCase workflowUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC;
        this.getUC = getUC; this.listUC = listUC;
        this.listDeletedUC = listDeletedUC; this.restoreUC = restoreUC; this.purgeUC = purgeUC;
        this.importBulkUC = importBulkUC; this.handoverBulkUC = handoverBulkUC; this.workflowUC = workflowUC;
    }

    /** Tạo mới khách hàng. @param cmd JSON body @return 201 */
    @PreAuthorize("hasAuthority('customer.create')")
    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResult>> create(@Valid @RequestBody CreateCustomerCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(cmd)));
    }

    /** Lấy danh sách khách hàng có phân trang. @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CustomerResult>>> list(
            HttpServletRequest req,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy, @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String q, @RequestParam(required = false) String status) {
        Integer fromYear = (Integer) req.getAttribute("dataAccessFromYear");
        // Record-level visibility: admin/manager xem tat ca, nhan vien chi xem ban ghi minh phu trach
        Long userId = (Long) req.getAttribute("userId");
        boolean privileged = SecurityUtils.isAdminOrManager(SecurityContextHolder.getContext().getAuthentication());
        Long ownerId = privileged ? null : userId;
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(listUC.execute(
                PageRequest.builder().page(page).size(size).sortBy(sortBy).sortDir(sortDir).dataAccessFromYear(fromYear).q(q).status(status).ownerId(ownerId).build()))));
    }

    /** Lấy khách hàng theo ID. @param id ID @return 200 */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResult>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(getUC.execute(id)));
    }

    /** Cập nhật khách hàng. @param id ID @param cmd JSON body @return 200 */
    @PreAuthorize("hasAuthority('customer.edit')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResult>> update(@PathVariable Long id,
                                                               @Valid @RequestBody UpdateCustomerCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateCustomerCommand.builder().id(id).name(cmd.getName()).shortName(cmd.getShortName())
                        .type(cmd.getType())
                        .taxCode(cmd.getTaxCode()).phone(cmd.getPhone()).email(cmd.getEmail())
                        .website(cmd.getWebsite()).address(cmd.getAddress())
                        .industry(cmd.getIndustry()).source(cmd.getSource())
                        .creditDays(cmd.getCreditDays()).creditLimit(cmd.getCreditLimit())
                        .rating(cmd.getRating())
                        .employeeSize(cmd.getEmployeeSize()).isDistributor(cmd.getIsDistributor())
                        .ownerId(cmd.getOwnerId()).build())));
    }

    /** Xóa mềm khách hàng. @param id ID @param req HTTP request @return 204 */
    @PreAuthorize("hasAuthority('customer.delete')")
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
    @PreAuthorize("hasAuthority('customer.delete')")
    @PostMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<Void>> restore(@PathVariable Long id) {
        restoreUC.execute(id); return ResponseEntity.ok(ApiResponse.ok(null));
    }

    /** Xóa vĩnh viễn khách hàng khỏi thùng rác. @param id ID @return 200 */
    @PreAuthorize("hasAuthority('customer.delete')")
    @DeleteMapping("/{id}/purge")
    public ResponseEntity<ApiResponse<Void>> purge(@PathVariable Long id) {
        purgeUC.execute(id); return ResponseEntity.ok(ApiResponse.ok(null));
    }

    /** Nhập hàng loạt khách hàng từ file. @param cmd body @return 200 */
    @PreAuthorize("hasAuthority('customer.import')")
    @PostMapping("/import-bulk")
    public ResponseEntity<ApiResponse<ImportBulkResult>> importBulk(@Valid @RequestBody ImportBulkCustomerCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(importBulkUC.execute(cmd)));
    }

    /** Kích hoạt khách hàng (→ active). @param id ID @return 200 */
    @PreAuthorize("hasAuthority('customer.activate')")
    @PostMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<CustomerResult>> activate(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.activate(id)));
    }

    /** Ngừng hoạt động khách hàng (→ inactive). @param id ID @return 200 */
    @PreAuthorize("hasAuthority('customer.activate')")
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<CustomerResult>> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.deactivate(id)));
    }

    /** Bàn giao hàng loạt khách hàng sang người dùng khác. @param body body @param req HTTP request @return 200 */
    @PostMapping("/handover-bulk")
    public ResponseEntity<ApiResponse<Void>> handoverBulk(@Valid @RequestBody HandoverBulkRequest body, HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        boolean isAdminOrManager = SecurityUtils.isAdminOrManager(SecurityContextHolder.getContext().getAuthentication());
        handoverBulkUC.execute(HandoverBulkCommand.builder()
                .ids(body.getIds()).toUserId(body.getToUserId())
                .currentUserId(userId).adminOrManager(isAdminOrManager)
                .reason(body.getReason()).build());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
