package vn.com.be_crm.application.quotation.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Dữ liệu báo giá hiển thị trên trang phản hồi công khai của khách (không cần đăng nhập).
 *
 * @param code                 mã báo giá
 * @param customerName         tên khách hàng
 * @param contactName          tên người liên hệ
 * @param quoteDate            ngày báo giá
 * @param validUntil           hiệu lực đến
 * @param currency             loại tiền tệ
 * @param note                 ghi chú
 * @param subtotal             tạm tính
 * @param discount             chiết khấu
 * @param tax                  thuế
 * @param total                tổng cộng
 * @param status               trạng thái báo giá
 * @param customerResponse     phản hồi hiện có của khách (null nếu chưa phản hồi)
 * @param customerResponseNote nội dung phản hồi đã gửi
 * @param items                danh sách dòng hàng
 */
public record PublicQuotationView(
        String code,
        String customerName,
        String contactName,
        LocalDate quoteDate,
        LocalDate validUntil,
        String currency,
        String note,
        BigDecimal subtotal,
        BigDecimal discount,
        BigDecimal tax,
        BigDecimal total,
        String status,
        String customerResponse,
        String customerResponseNote,
        List<Line> items
) {
    /**
     * Dòng hàng hiển thị công khai.
     *
     * @param productName tên sản phẩm
     * @param unit        đơn vị tính
     * @param quantity    số lượng
     * @param unitPrice   đơn giá
     * @param discount    chiết khấu
     * @param amount      thành tiền
     */
    public record Line(
            String productName,
            String unit,
            BigDecimal quantity,
            BigDecimal unitPrice,
            BigDecimal discount,
            BigDecimal amount
    ) {}
}
