package vn.com.be_crm.application.dashboard.dto;

import java.math.BigDecimal;

/**
 * Một phần của biểu đồ tròn / thanh xếp chồng — kèm số lượng và tỉ lệ phần trăm.
 *
 * @param label nhãn (trạng thái, vai trò, đơn vị...)
 * @param count số lượng bản ghi
 * @param pct   tỉ lệ phần trăm trên tổng (0–100)
 */
public record DonutSegment(String label, long count, BigDecimal pct) {
}
