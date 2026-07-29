package vn.com.be_crm.presentation.auditlog;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.auditlog.dto.AuditLogEntry;
import vn.com.be_crm.application.auditlog.query.ListAuditLogUseCase;
import vn.com.be_crm.presentation.shared.ApiResponse;
import vn.com.be_crm.presentation.shared.PageResponse;

/**
 * REST controller cho nhật ký sự kiện thao tác người dùng — chỉ ADMIN (guard tại {@code SecurityConfig},
 * URL {@code /api/audit-log/**}). Đọc-only, gộp từ các bảng đã có sẵn trong DB, không có bảng riêng.
 */
@RestController
@RequestMapping("/api/audit-log")
public class AuditLogController {

    private final ListAuditLogUseCase listUC;

    /** @param listUC liệt kê nhật ký */
    public AuditLogController(ListAuditLogUseCase listUC) {
        this.listUC = listUC;
    }

    /**
     * Lấy danh sách nhật ký sự kiện, mới nhất trước.
     *
     * @param source lọc theo nguồn (quotation_approval/lead_transfer/ticket_comment/notification/record_change)
     * @param q      tìm kiếm theo bản ghi đích/ghi chú/người thực hiện
     * @param page   trang (mặc định 0)
     * @param size   số dòng mỗi trang (mặc định 20)
     * @return 200
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AuditLogEntry>>> list(
            @RequestParam(required = false) String source, @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(listUC.execute(source, q, page, size))));
    }
}
