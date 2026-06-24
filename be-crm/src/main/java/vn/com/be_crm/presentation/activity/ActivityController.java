package vn.com.be_crm.presentation.activity;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    /**
     * @param createUseCase use case tạo mới
     * @param updateUseCase use case cập nhật
     * @param deleteUseCase use case xóa
     * @param getUseCase    use case lấy theo ID
     * @param listUseCase   use case lấy danh sách
     * @param importBulkUC  use case nhập hàng loạt
     */
    public ActivityController(CreateActivityUseCase createUseCase, UpdateActivityUseCase updateUseCase,
            DeleteActivityUseCase deleteUseCase, GetActivityUseCase getUseCase,
            ListActivityUseCase listUseCase, ImportBulkActivityUseCase importBulkUC) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.deleteUseCase = deleteUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
        this.importBulkUC = importBulkUC;
    }

    /**
     * Tạo mới hoạt động.
     *
     * @param request JSON body tạo mới
     * @return 201 Created với ActivityResult
     */
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
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Integer fromYear = (Integer) req.getAttribute("dataAccessFromYear");
        PageResult<ActivityResult> result = listUseCase.execute(
                PageRequest.builder().page(page).size(size).sortBy(sortBy).sortDir(sortDir).dataAccessFromYear(fromYear)
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
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    /** Nhập hàng loạt hoạt động từ file. @param cmd body @return 200 */
    @PostMapping("/import-bulk")
    public ResponseEntity<ApiResponse<ImportBulkResult>> importBulk(@Valid @RequestBody ImportBulkActivityCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(importBulkUC.execute(cmd)));
    }
}
