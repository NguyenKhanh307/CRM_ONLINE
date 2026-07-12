package vn.com.be_crm.application.related.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Một dòng bản ghi liên quan trên trang chi tiết 360° — shape chung cho mọi phân hệ
 * (liên hệ, cơ hội, báo giá, đơn hàng, hóa đơn, phiếu chăm sóc, hoạt động).
 * FE chọn hiển thị cột nào theo từng tab.
 *
 * @param module    khóa phân hệ (contact/opportunity/quotation/order/invoice/ticket/activity) — FE dùng để deep-link
 * @param id        ID bản ghi
 * @param code      mã bản ghi (hoặc giá trị phụ: email của liên hệ, loại của hoạt động)
 * @param name      tên/tiêu đề bản ghi
 * @param status    trạng thái (hoặc nhãn phụ: chức danh của liên hệ)
 * @param date      mốc thời gian chính của bản ghi
 * @param amount    số tiền (null với bản ghi không có tiền)
 * @param ownerName tên người phụ trách (đã resolve sẵn ở SQL)
 */
public record RelatedRecord(
        String module,
        Long id,
        String code,
        String name,
        String status,
        LocalDateTime date,
        BigDecimal amount,
        String ownerName) {
}
