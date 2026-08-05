package vn.com.be_crm.application.quotation.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// dữ liệu báo giá hiển thị trên trang phản hồi công khai của khách (không cần đăng nhập).
// subtotal/discount/tax/total/amount không còn là cột lưu sẵn — BE tính từ dòng hàng
// (LineItemTotals) ngay tại use case rồi mới đóng gói vào view này.
public record PublicQuotationView(
        String code,
        String customerName,
        String contactName,
        LocalDate quoteDate,
        LocalDate validUntil,
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
    // dòng hàng hiển thị công khai: productName/unit/quantity/unitPrice/discount/amount (thành tiền)
    public record Line(
            String productName,
            String unit,
            BigDecimal quantity,
            BigDecimal unitPrice,
            BigDecimal discount,
            BigDecimal amount
    ) {}
}
