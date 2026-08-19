package vn.com.be_crm.presentation.service;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.service.command.CreateSlaPolicyUseCase;
import vn.com.be_crm.application.service.command.DeleteSlaPolicyUseCase;
import vn.com.be_crm.application.service.command.UpdateSlaPolicyUseCase;
import vn.com.be_crm.application.service.dto.CreateSlaPolicyCommand;
import vn.com.be_crm.application.service.dto.SlaPolicyResult;
import vn.com.be_crm.application.service.dto.UpdateSlaPolicyCommand;
import vn.com.be_crm.application.service.query.GetSlaPolicyUseCase;
import vn.com.be_crm.application.service.query.ListSlaPolicyUseCase;
import vn.com.be_crm.core.response.ApiResponse;

import java.util.List;

/**
 * REST controller cho chính sách SLA.
 */
@RestController
@RequestMapping("/api/sla-policies")
public class SlaPolicyController {
    private final CreateSlaPolicyUseCase createUC;
    private final UpdateSlaPolicyUseCase updateUC;
    private final DeleteSlaPolicyUseCase deleteUC;
    private final GetSlaPolicyUseCase getUC;
    private final ListSlaPolicyUseCase listUC;

    /**
     * @param createUC use case tạo mới @param updateUC use case cập nhật @param deleteUC use case xóa
     * @param getUC    use case lấy theo ID @param listUC use case lấy danh sách
     */
    public SlaPolicyController(CreateSlaPolicyUseCase createUC, UpdateSlaPolicyUseCase updateUC,
                                DeleteSlaPolicyUseCase deleteUC, GetSlaPolicyUseCase getUC, ListSlaPolicyUseCase listUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC;
        this.getUC = getUC; this.listUC = listUC;
    }

    /** Tạo mới chính sách SLA. @param cmd JSON body @return 201 */
    @PreAuthorize("hasAuthority('sla.create')")
    @PostMapping
    public ResponseEntity<ApiResponse<SlaPolicyResult>> create(@Valid @RequestBody CreateSlaPolicyCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(cmd)));
    }

    /** Lấy toàn bộ chính sách SLA. @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<List<SlaPolicyResult>>> list() {
        return ResponseEntity.ok(ApiResponse.ok(listUC.execute()));
    }

    /** Lấy chính sách SLA theo ID. @param id ID @return 200 */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SlaPolicyResult>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(getUC.execute(id)));
    }

    /** Cập nhật chính sách SLA. @param id ID @param cmd JSON body @return 200 */
    @PreAuthorize("hasAuthority('sla.edit')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SlaPolicyResult>> update(@PathVariable Long id, @Valid @RequestBody UpdateSlaPolicyCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateSlaPolicyCommand.builder().id(id).name(cmd.getName()).priority(cmd.getPriority())
                        .firstResponseHours(cmd.getFirstResponseHours()).resolutionHours(cmd.getResolutionHours())
                        .isActive(cmd.getIsActive()).build())));
    }

    /** Xóa chính sách SLA. @param id ID @return 204 */
    @PreAuthorize("hasAuthority('sla.delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUC.execute(id); return ResponseEntity.noContent().build();
    }
}
