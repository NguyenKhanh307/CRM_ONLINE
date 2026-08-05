package vn.com.be_crm.application.auditlog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// một dòng nhật ký sự kiện thao tác người dùng — gộp từ nhiều bảng đã có trong DB
// (quotation_approvals, notifications, created_by/updated_by của các bảng nghiệp vụ chính).
// KHÔNG có bảng riêng cho DTO này — chỉ đọc-only
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogEntry {

    // nguồn dữ liệu: quotation_approval | notification | record_change
    private String source;

    // người thực hiện (có thể null — vd sự kiện hệ thống/khách vãng lai)
    private String actorName;

    // hành động ngắn gọn (vd "Duyệt báo giá", "Sửa bản ghi")
    private String action;

    // bản ghi đích (vd "Báo giá BG-001")
    private String targetLabel;

    // ghi chú/lý do đi kèm (có thể null)
    private String note;

    // thời điểm xảy ra
    private LocalDateTime occurredAt;
}
