package vn.com.be_crm.application.auditlog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Một dòng nhật ký sự kiện thao tác người dùng — gộp từ nhiều bảng đã có trong DB
 * (quotation_approvals, lead_transfers, ticket_comments, notifications, created_by/updated_by
 * của các bảng nghiệp vụ chính). KHÔNG có bảng riêng cho DTO này — chỉ đọc-only.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogEntry {

    /** Nguồn dữ liệu: quotation_approval | lead_transfer | ticket_comment | notification | record_change. */
    private String source;

    /** Người thực hiện (có thể null — vd sự kiện hệ thống/khách vãng lai). */
    private String actorName;

    /** Hành động ngắn gọn (vd "Duyệt báo giá", "Bàn giao tiềm năng cho X", "Sửa bản ghi"). */
    private String action;

    /** Bản ghi đích (vd "Báo giá BG-001"). */
    private String targetLabel;

    /** Ghi chú/lý do đi kèm (có thể null). */
    private String note;

    /** Thời điểm xảy ra. */
    private LocalDateTime occurredAt;
}
