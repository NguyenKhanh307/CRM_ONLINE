package vn.com.be_crm.application.shared.pdf;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Dữ liệu đầu vào để sinh PDF bảng báo giá (application layer không phụ thuộc thư viện PDF).
 *
 * @param code         mã báo giá
 * @param customerName tên khách hàng
 * @param contactName  tên người liên hệ
 * @param quoteDate    ngày báo giá
 * @param validUntil   hiệu lực đến
 * @param currency     loại tiền tệ
 * @param note         ghi chú
 * @param total        tổng cộng
 * @param lines        danh sách dòng hàng
 */
public record QuotationPdfData(
        String code,
        String customerName,
        String contactName,
        LocalDate quoteDate,
        LocalDate validUntil,
        String currency,
        String note,
        BigDecimal total,
        List<Line> lines
) {
    /**
     * Một dòng hàng trong bảng báo giá.
     *
     * @param stt         số thứ tự
     * @param productName tên sản phẩm
     * @param unit        đơn vị tính
     * @param quantity    số lượng
     * @param unitPrice   đơn giá
     * @param discount    chiết khấu (số tiền)
     * @param amount      thành tiền
     */
    public record Line(
            int stt,
            String productName,
            String unit,
            BigDecimal quantity,
            BigDecimal unitPrice,
            BigDecimal discount,
            BigDecimal amount
    ) {}
}
