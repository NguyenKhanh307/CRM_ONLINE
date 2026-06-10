package vn.com.be_crm.application.shared.dto;

import java.time.LocalDateTime;

/**
 * DTO dùng chung cho kết quả danh sách thùng rác của mọi module.
 *
 * @param id            ID bản ghi gốc
 * @param displayName   tên / mã hiển thị trong thùng rác
 * @param deletedAt     thời điểm xóa
 * @param deletedByName họ tên người đã xóa
 */
public record DeletedItemResult(
        Long id,
        String displayName,
        LocalDateTime deletedAt,
        String deletedByName
) {}
