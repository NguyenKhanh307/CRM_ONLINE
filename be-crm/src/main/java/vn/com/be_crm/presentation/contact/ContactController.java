package vn.com.be_crm.presentation.contact;

import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.contact.command.*;
import vn.com.be_crm.application.contact.dto.*;
import vn.com.be_crm.application.contact.query.*;
import vn.com.be_crm.core.dto.delete.DeleteCommand;
import vn.com.be_crm.core.dto.delete.DeletedItemResult;
import vn.com.be_crm.core.dto.import_.ImportBulkResult;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.util.SecurityUtils;
import vn.com.be_crm.core.response.ApiResponse;
import vn.com.be_crm.core.page.PageResponse;

/**
 * REST controller cho nghiệp vụ quản lý liên hệ.
 */
@RestController
@RequestMapping("/api/contacts")
public class ContactController {
    private final CreateContactUseCase createUC;
    private final UpdateContactUseCase updateUC;
    private final DeleteContactUseCase deleteUC;
    private final GetContactUseCase getUC;
    private final ListContactUseCase listUC;
    private final ListDeletedContactsUseCase listDeletedUC;
    private final RestoreContactUseCase restoreUC;
    private final PurgeContactUseCase purgeUC;
    private final ImportBulkContactUseCase importBulkUC;

    /**
     * @param createUC use case tạo mới @param updateUC use case cập nhật @param deleteUC use case xóa mềm
     * @param getUC    use case lấy theo ID @param listUC use case lấy danh sách
     * @param listDeletedUC thùng rác @param restoreUC khôi phục @param purgeUC xóa vĩnh viễn @param importBulkUC nhập hàng loạt
     */
    public ContactController(CreateContactUseCase createUC, UpdateContactUseCase updateUC,
                              DeleteContactUseCase deleteUC, GetContactUseCase getUC, ListContactUseCase listUC,
                              ListDeletedContactsUseCase listDeletedUC, RestoreContactUseCase restoreUC, PurgeContactUseCase purgeUC,
                              ImportBulkContactUseCase importBulkUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC;
        this.getUC = getUC; this.listUC = listUC;
        this.listDeletedUC = listDeletedUC; this.restoreUC = restoreUC; this.purgeUC = purgeUC;
        this.importBulkUC = importBulkUC;
    }

    /** Tạo mới liên hệ. @param cmd JSON body @return 201 */
    @PreAuthorize("hasAuthority('contact.create')")
    @PostMapping
    public ResponseEntity<ApiResponse<ContactResult>> create(@Valid @RequestBody CreateContactCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(cmd)));
    }

    /** Lấy danh sách liên hệ có phân trang. @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ContactResult>>> list(
            HttpServletRequest req,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy, @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String q, @RequestParam(required = false) String status,
            @RequestParam(required = false) Long customerId) {
        Integer fromYear = (Integer) req.getAttribute("dataAccessFromYear");
        // Record-level visibility: admin/manager xem tat ca, nhan vien chi xem ban ghi minh phu trach
        Long userId = (Long) req.getAttribute("userId");
        boolean privileged = SecurityUtils.isAdminOrManager(SecurityContextHolder.getContext().getAuthentication());
        Long ownerId = privileged ? null : userId;
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(listUC.execute(
                PageRequest.builder().page(page).size(size).sortBy(sortBy).sortDir(sortDir).dataAccessFromYear(fromYear).q(q).status(status).ownerId(ownerId).customerId(customerId).build()))));
    }

    /** Lấy liên hệ theo ID. @param id ID @return 200 */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContactResult>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(getUC.execute(id)));
    }

    /** Cập nhật liên hệ. @param id ID @param cmd JSON body @return 200 */
    @PreAuthorize("hasAuthority('contact.edit')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ContactResult>> update(@PathVariable Long id,
                                                              @Valid @RequestBody UpdateContactCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateContactCommand.builder().id(id).customerId(cmd.getCustomerId())
                        .assignedUserId(cmd.getAssignedUserId()).salutation(cmd.getSalutation())
                        .fullName(cmd.getFullName()).title(cmd.getTitle()).department(cmd.getDepartment())
                        .email(cmd.getEmail())
                        .zalo(cmd.getZalo()).source(cmd.getSource()).gender(cmd.getGender())
                        .dateOfBirth(cmd.getDateOfBirth())
                        .isPrimary(cmd.getIsPrimary()).build())));
    }

    /** Xóa mềm liên hệ. @param id ID @param req HTTP request @return 204 */
    @PreAuthorize("hasAuthority('contact.delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        deleteUC.execute(new DeleteCommand(id, userId));
        return ResponseEntity.noContent().build();
    }

    /** Lấy danh sách liên hệ trong thùng rác. @return 200 */
    @GetMapping("/deleted")
    public ResponseEntity<ApiResponse<PageResponse<DeletedItemResult>>> listDeleted(
            HttpServletRequest req,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Long userId = (Long) req.getAttribute("userId");
        boolean isAdmin = SecurityUtils.isAdmin(SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(listDeletedUC.execute(userId, isAdmin,
                PageRequest.builder().page(page).size(size).sortBy("deletedAt").sortDir("desc").build()))));
    }

    /** Khôi phục liên hệ từ thùng rác. @param id ID @return 200 */
    @PreAuthorize("hasAuthority('contact.delete')")
    @PostMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<Void>> restore(@PathVariable Long id) {
        restoreUC.execute(id); return ResponseEntity.ok(ApiResponse.ok(null));
    }

    /** Xóa vĩnh viễn liên hệ khỏi thùng rác. @param id ID @return 200 */
    @PreAuthorize("hasAuthority('contact.delete')")
    @DeleteMapping("/{id}/purge")
    public ResponseEntity<ApiResponse<Void>> purge(@PathVariable Long id) {
        purgeUC.execute(id); return ResponseEntity.ok(ApiResponse.ok(null));
    }

    /** Nhập hàng loạt liên hệ từ file. @param cmd body @return 200 */
    @PreAuthorize("hasAuthority('contact.import')")
    @PostMapping("/import-bulk")
    public ResponseEntity<ApiResponse<ImportBulkResult>> importBulk(@Valid @RequestBody ImportBulkContactCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(importBulkUC.execute(cmd)));
    }
}
