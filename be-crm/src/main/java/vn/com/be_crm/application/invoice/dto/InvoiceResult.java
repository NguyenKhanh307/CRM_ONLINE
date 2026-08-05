package vn.com.be_crm.application.invoice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.be_crm.domain.invoice.enums.InvoiceStatus;
import vn.com.be_crm.domain.invoice.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// output cho Invoice. customerId/contactId/quotationId/opportunityId/campaignId không còn — mọi
// liên kết tra qua orderId. subtotal/discount/tax/total tính từ dòng hàng tại thời điểm đọc.
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class InvoiceResult {
    private Long id;
    private String code;
    private Long orderId;
    private Long ownerId;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private InvoiceStatus status;
    private PaymentStatus paymentStatus;
    private Boolean isLocked;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal total;
    private String note;
    // Audit: BE tự đóng dấu (AuditInterceptor), client không gửi lên.
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Tên khóa ngoại — do BE resolve (INameResolver).
    private String orderCode;
    private String ownerName;
    // Tên người tạo/người sửa — do BE resolve (INameResolver).
    private String createdByName;
    private String updatedByName;
}
