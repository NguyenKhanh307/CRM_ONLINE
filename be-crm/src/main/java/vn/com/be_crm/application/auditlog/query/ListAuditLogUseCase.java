package vn.com.be_crm.application.auditlog.query;

import vn.com.be_crm.application.auditlog.dto.AuditLogEntry;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.auditlog.repository.IAuditLogRepository;

/**
 * Use case liệt kê nhật ký sự kiện thao tác người dùng cho ADMIN (đọc-only, gộp từ các bảng đã có).
 */
public class ListAuditLogUseCase {

    private final IAuditLogRepository repo;

    /** @param repo port đọc nhật ký */
    public ListAuditLogUseCase(IAuditLogRepository repo) {
        this.repo = repo;
    }

    /**
     * @param source lọc theo nguồn — null = tất cả
     * @param q      tìm kiếm theo bản ghi đích/ghi chú/người thực hiện — null = không lọc
     * @param page   trang (bắt đầu từ 0)
     * @param size   số dòng mỗi trang
     * @return trang kết quả
     */
    public PageResult<AuditLogEntry> execute(String source, String q, int page, int size) {
        return repo.list(source, q, page, size);
    }
}
