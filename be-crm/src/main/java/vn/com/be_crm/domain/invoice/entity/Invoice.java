package vn.com.be_crm.domain.invoice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.invoice.enums.InvoiceStatus;
import vn.com.be_crm.domain.invoice.enums.PaymentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

// hóa đơn (chứng từ tài chính cuối cùng, kế thừa từ đơn hàng). Không còn customerId/contactId/
// quotationId/opportunityId/campaignId trực tiếp — chỉ còn orderId, mọi liên kết khác tra qua
// chuỗi Invoice -> Order -> Quotation. subtotal/discount/tax/total không còn cột lưu sẵn —
// tính từ dòng hàng tại thời điểm đọc (xem LineItemTotals).
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {
    private Long id;
    private String code;
    // đơn hàng nguồn (1-1) — mọi liên kết khách hàng/liên hệ/báo giá/cơ hội tra qua đây
    private Long orderId;
    private Long ownerId;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private InvoiceStatus status;
    // trạng thái thanh toán (suy ra từ các đợt thanh toán)
    private PaymentStatus paymentStatus;
    // khóa dữ liệu khi đã phát hành (read-only)
    private boolean isLocked;
    private String note;
    // createdBy/updatedBy do BE tự đóng dấu (audit), client không gửi lên
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long deletedBy;
    private boolean isPurged;
}
