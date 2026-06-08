package vn.com.be_crm.presentation.contact;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.contact.command.*;
import vn.com.be_crm.application.contact.dto.*;
import vn.com.be_crm.application.contact.query.*;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.presentation.shared.ApiResponse;
import vn.com.be_crm.presentation.shared.PageResponse;

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

    /**
     * @param createUC use case tạo mới @param updateUC use case cập nhật @param deleteUC use case xóa mềm
     * @param getUC    use case lấy theo ID @param listUC use case lấy danh sách
     */
    public ContactController(CreateContactUseCase createUC, UpdateContactUseCase updateUC,
                              DeleteContactUseCase deleteUC, GetContactUseCase getUC, ListContactUseCase listUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC;
        this.getUC = getUC; this.listUC = listUC;
    }

    /** Tạo mới liên hệ. @param cmd JSON body @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<ContactResult>> create(@Valid @RequestBody CreateContactCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(cmd)));
    }

    /** Lấy danh sách liên hệ có phân trang. @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ContactResult>>> list(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(listUC.execute(
                PageRequest.builder().page(page).size(size).sortBy(sortBy).sortDir(sortDir).build()))));
    }

    /** Lấy liên hệ theo ID. @param id ID @return 200 */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContactResult>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(getUC.execute(id)));
    }

    /** Cập nhật liên hệ. @param id ID @param cmd JSON body @return 200 */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ContactResult>> update(@PathVariable Long id,
                                                              @Valid @RequestBody UpdateContactCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateContactCommand.builder().id(id).customerId(cmd.getCustomerId())
                        .assignedUserId(cmd.getAssignedUserId()).fullName(cmd.getFullName())
                        .position(cmd.getPosition()).email(cmd.getEmail()).gender(cmd.getGender())
                        .dateOfBirth(cmd.getDateOfBirth()).address(cmd.getAddress())
                        .isPrimary(cmd.getIsPrimary()).build())));
    }

    /** Xóa mềm liên hệ. @param id ID @return 204 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUC.execute(id); return ResponseEntity.noContent().build();
    }
}
