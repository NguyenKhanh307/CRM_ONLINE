package vn.com.be_crm.presentation.activity;

import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.infrastructure.shared.util.SecurityUtils;
import vn.com.be_crm.application.activity.command.*;
import vn.com.be_crm.application.activity.dto.*;
import vn.com.be_crm.application.activity.query.*;
import vn.com.be_crm.application.shared.dto.ImportBulkResult;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.presentation.activity.request.*;
import vn.com.be_crm.presentation.shared.ApiResponse;
import vn.com.be_crm.presentation.shared.PageResponse;

/**
 * REST controller cho nghiệp vụ quản lý hoạt động chăm sóc.
 */
@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final CreateActivityUseCase createUseCase;
    private final UpdateActivityUseCase updateUseCase;
    private final DeleteActivityUseCase deleteUseCase;
    private final GetActivityUseCase getUseCase;
    private final ListActivityUseCase listUseCase;
    private final ImportBulkActivityUseCase importBulkUC;
    private final ActivityWorkflowUseCase workflowUC;

    /**
     * @param createUseCase use case tạo mới
     * @param updateUseCase use case cập nhật
     * @param deleteUseCase use case xóa
     * @param getUseCase    use case lấy theo ID
     * @param listUseCase   use case lấy danh sách
     * @param importBulkUC  use case nhập hàng loạt
     * @param workflowUC    use case luồng trạng thái hoạt động
     */
    public ActivityController(CreateActivityUseCase createUseCase, UpdateActivityUseCase updateUseCase,
            DeleteActivityUseCase deleteUseCase, GetActivityUseCase getUseCase,
            ListActivityUseCase listUseCase, ImportBulkActivityUseCase importBulkUC,
            ActivityWorkflowUseCase workflowUC) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.deleteUseCase = deleteUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
        this.importBulkUC = importBulkUC;
        this.workflowUC = workflowUC;
    }

    /**
     * Tạo mới hoạt động.
     *
     * @param request JSON body tạo mới
     * @return 201 Created với ActivityResult
     */
    @PreAuthorize("hasAuthority('activity.create')")
    @PostMapping
    public ResponseEntity<ApiResponse<ActivityResult>> create(@Valid @RequestBody CreateActivityRequest request) {
        CreateActivityCommand cmd = CreateActivityCommand.builder()
                .type(request.getType()).subject(request.getSubject()).content(request.getContent())
                .priority(request.getPriority())
                .targetType(request.getTargetType()).targetId(request.getTargetId())
                .relatedType(request.getRelatedType()).relatedId(request.getRelatedId())
                .location(request.getLocation()).callDirection(request.getCallDirection())
                .callResult(request.getCallResult()).callDuration(request.getCallDuration())
                .assignedUserId(request.getAssignedUserId()).status(request.getStatus())
                .dueAt(request.getDueAt()).build();
        return ResponseEntity.status(201).body(ApiResponse.created(createUseCase.execute(cmd)));
    }

    /**
     * Lấy danh sách hoạt động có phân trang.
     *
     * @param page    số trang
     * @param size    số bản ghi mỗi trang
     * @param sortBy  field sắp xếp
     * @param sortDir chiều sắp xếp
     * @return 200 OK với PageResponse
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ActivityResult>>> list(
            HttpServletRequest req,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String q, @RequestParam(required = false) String status) {
        Integer fromYear = (Integer) req.getAttribute("dataAccessFromYear");
        // Record-level visibility: admin/manager xem tất cả, nhân viên chỉ xem hoạt động mình phụ trách
        Long userId = (Long) req.getAttribute("userId");
        boolean privileged = SecurityUtils.isAdminOrManager(SecurityContextHolder.getContext().getAuthentication());
        Long ownerId = privileged ? null : userId;
        PageResult<ActivityResult> result = listUseCase.execute(
                PageRequest.builder().page(page).size(size).sortBy(sortBy).sortDir(sortDir).dataAccessFromYear(fromYear)
                        .q(q).status(status).ownerId(ownerId)
                        .build());
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(result)));
    }

    /**
     * Lấy hoạt động theo ID.
     *
     * @param id ID hoạt động
     * @return 200 OK với ActivityResult
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ActivityResult>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(getUseCase.execute(id)));
    }

    /**
     * Cập nhật hoạt động.
     *
     * @param id      ID hoạt động cần cập nhật
     * @param request JSON body cập nhật
     * @return 200 OK với ActivityResult đã cập nhật
     */
    @PreAuthorize("hasAuthority('activity.edit')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ActivityResult>> update(@PathVariable Long id,
            @Valid @RequestBody UpdateActivityRequest request) {
        UpdateActivityCommand cmd = UpdateActivityCommand.builder()
                .id(id).type(request.getType()).subject(request.getSubject()).content(request.getContent())
                .priority(request.getPriority()).location(request.getLocation())
                .callDirection(request.getCallDirection()).callResult(request.getCallResult())
                .callDuration(request.getCallDuration())
                .assignedUserId(request.getAssignedUserId()).status(request.getStatus())
                .dueAt(request.getDueAt()).completedAt(request.getCompletedAt()).build();
        return ResponseEntity.ok(ApiResponse.ok(updateUseCase.execute(cmd)));
    }

    /**
     * Xóa hoạt động.
     *
     * @param id ID hoạt động cần xóa
     * @return 204 No Content
     */
    @PreAuthorize("hasAuthority('activity.delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    /** Nhập hàng loạt hoạt động từ file. @param cmd body @return 200 */
    @PreAuthorize("hasAuthority('activity.import')")
    @PostMapping("/import-bulk")
    public ResponseEntity<ApiResponse<ImportBulkResult>> importBulk(@Valid @RequestBody ImportBulkActivityCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(importBulkUC.execute(cmd)));
    }

    /** Bắt đầu thực hiện hoạt động (planned → in_progress). @param id ID @return 200 */
    @PostMapping("/{id}/start")
    public ResponseEntity<ApiResponse<ActivityResult>> start(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.start(id)));
    }

    /** Hoàn thành hoạt động (→ done). @param id ID @return 200 */
    @PostMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<ActivityResult>> complete(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.complete(id)));
    }

    /** Hủy hoạt động (→ cancelled). @param id ID @return 200 */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<ActivityResult>> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(workflowUC.cancel(id)));
    }
}
