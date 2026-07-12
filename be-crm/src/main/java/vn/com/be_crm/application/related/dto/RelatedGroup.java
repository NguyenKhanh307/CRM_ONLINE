package vn.com.be_crm.application.related.dto;

import java.util.List;

/**
 * Nhóm bản ghi liên quan của một phân hệ: danh sách rút gọn (tối đa {@code LIMIT} dòng gần nhất)
 * kèm tổng số thật để FE hiện badge và câu "còn N bản ghi — xem trong danh sách".
 *
 * @param items danh sách bản ghi (mới nhất trước)
 * @param total tổng số bản ghi thật trong DB (có thể lớn hơn items.size())
 */
public record RelatedGroup(List<RelatedRecord> items, long total) {
}
