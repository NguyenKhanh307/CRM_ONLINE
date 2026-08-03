package vn.com.be_crm.domain.auditlog.repository;

import vn.com.be_crm.application.auditlog.dto.AuditLogEntry;
import vn.com.be_crm.core.page.PageResult;

/**
 * Port đọc nhật ký sự kiện thao tác người dùng — gộp từ các bảng đã có sẵn trong DB.
 * Chỉ đọc, không có lệnh ghi (không có bảng riêng cho nhật ký).
 */
public interface IAuditLogRepository {

    /**
     * Liệt kê nhật ký sự kiện, mới nhất trước.
     *
     * @param source lọc theo nguồn (quotation_approval/lead_transfer/ticket_comment/notification/record_change) — null = tất cả
     * @param q      tìm kiếm theo bản ghi đích/ghi chú — null/rỗng = không lọc
     * @param page   trang (bắt đầu từ 0)
     * @param size   số dòng mỗi trang
     * @return trang kết quả
     */
    PageResult<AuditLogEntry> list(String source, String q, int page, int size);
}
