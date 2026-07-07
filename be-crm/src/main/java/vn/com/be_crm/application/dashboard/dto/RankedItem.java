package vn.com.be_crm.application.dashboard.dto;

import java.math.BigDecimal;

/**
 * Một mục trong danh sách xếp hạng theo giá trị (vd cơ hội giá trị lớn, doanh thu theo nhân viên).
 *
 * @param refId ID bản ghi tham chiếu (nếu có)
 * @param label nhãn hiển thị
 * @param value giá trị dùng để xếp hạng
 */
public record RankedItem(Long refId, String label, BigDecimal value) {
}
