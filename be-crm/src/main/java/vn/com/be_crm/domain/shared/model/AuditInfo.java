package vn.com.be_crm.domain.shared.model;

import java.time.LocalDateTime;

/**
 * Value object chứa thông tin audit chung cho các entity có created_at, updated_at, deleted_at.
 *
 * @param createdAt  thời điểm tạo
 * @param updatedAt  thời điểm cập nhật gần nhất
 * @param deletedAt  thời điểm xóa mềm (null nếu chưa xóa)
 * @param deletedBy  ID người dùng đã xóa (null nếu chưa xóa)
 * @param isPurged   true nếu đã ẩn khỏi thùng rác (vẫn là soft-delete, không hiện trong UI)
 */
public record AuditInfo(
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt,
        Long deletedBy,
        boolean isPurged
) {
    /** Tạo AuditInfo cho entity mới (chưa xóa). */
    public static AuditInfo of(LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new AuditInfo(createdAt, updatedAt, null, null, false);
    }
}
