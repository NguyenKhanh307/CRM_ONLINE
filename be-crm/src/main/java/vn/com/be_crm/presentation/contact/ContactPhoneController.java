package vn.com.be_crm.presentation.contact;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.contact.command.*;
import vn.com.be_crm.application.contact.dto.*;
import vn.com.be_crm.application.contact.query.ListContactPhoneUseCase;
import vn.com.be_crm.presentation.shared.ApiResponse;

import java.util.List;

/**
 * REST controller cho nghiệp vụ quản lý số điện thoại liên hệ.
 */
@RestController
@RequestMapping("/api/contacts/{contactId}/phones")
public class ContactPhoneController {
    private final CreateContactPhoneUseCase createUC;
    private final UpdateContactPhoneUseCase updateUC;
    private final DeleteContactPhoneUseCase deleteUC;
    private final ListContactPhoneUseCase listUC;

    /**
     * @param createUC use case tạo mới @param updateUC use case cập nhật
     * @param deleteUC use case xóa @param listUC use case lấy danh sách
     */
    public ContactPhoneController(CreateContactPhoneUseCase createUC, UpdateContactPhoneUseCase updateUC,
                                   DeleteContactPhoneUseCase deleteUC, ListContactPhoneUseCase listUC) {
        this.createUC = createUC; this.updateUC = updateUC;
        this.deleteUC = deleteUC; this.listUC = listUC;
    }

    /** Tạo mới số điện thoại. @param contactId ID liên hệ @param cmd JSON body @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<ContactPhoneResult>> create(@PathVariable Long contactId,
                                                                   @Valid @RequestBody CreateContactPhoneCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(
                CreateContactPhoneCommand.builder().contactId(contactId).phone(cmd.getPhone())
                        .phoneType(cmd.getPhoneType()).isPrimary(cmd.getIsPrimary()).build())));
    }

    /** Lấy danh sách số điện thoại theo contactId. @param contactId ID liên hệ @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ContactPhoneResult>>> list(@PathVariable Long contactId) {
        return ResponseEntity.ok(ApiResponse.ok(listUC.execute(contactId)));
    }

    /** Cập nhật số điện thoại. @param id ID @param cmd JSON body @return 200 */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ContactPhoneResult>> update(@PathVariable Long contactId,
                                                                   @PathVariable Long id,
                                                                   @Valid @RequestBody UpdateContactPhoneCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateContactPhoneCommand.builder().id(id).phone(cmd.getPhone())
                        .phoneType(cmd.getPhoneType()).isPrimary(cmd.getIsPrimary()).build())));
    }

    /** Xóa số điện thoại. @param id ID @return 204 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long contactId, @PathVariable Long id) {
        deleteUC.execute(id); return ResponseEntity.noContent().build();
    }
}
