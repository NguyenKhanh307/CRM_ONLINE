package vn.com.be_crm.core.pdf.port;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// dữ liệu đầu vào để sinh PDF bảng báo giá (application layer không phụ thuộc thư viện PDF)
public record QuotationPdfData(
        String code,
        String customerName,
        String contactName,
        LocalDate quoteDate,
        LocalDate validUntil,
        String note,
        BigDecimal total,
        List<Line> lines
) {
    // một dòng hàng trong bảng báo giá: stt/productName/unit/quantity/unitPrice/discount/amount (thành tiền)
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
