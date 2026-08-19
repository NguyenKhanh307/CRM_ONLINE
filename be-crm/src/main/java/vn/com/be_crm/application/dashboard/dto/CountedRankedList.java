package vn.com.be_crm.application.dashboard.dto;

import java.util.List;

/**
 * Danh sách xếp hạng top-N kèm tổng số đầy đủ (tổng có thể lớn hơn số phần tử hiển thị).
 *
 * @param total tổng số bản ghi thỏa điều kiện
 * @param items top-N bản ghi để hiển thị chi tiết
 */
public record CountedRankedList(long total, List<RankedItem> items) {
}
