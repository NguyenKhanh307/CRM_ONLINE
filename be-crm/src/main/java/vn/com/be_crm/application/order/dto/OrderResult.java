package vn.com.be_crm.application.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.be_crm.domain.order.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// output cho Order. customerId/contactId/opportunityId/campaignId không còn — mọi liên kết tra
// qua quotationId. subtotal/discount/tax/total tính từ dòng hàng tại thời điểm đọc.
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class OrderResult {
    private Long id;
    private String code;
    private Long quotationId;
    private Long ownerId;
    private LocalDate orderDate;
    private LocalDate deliveryDate;
    private OrderStatus status;
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
    private String quotationCode;
    private String ownerName;
    // Tên người tạo/người sửa — do BE resolve (INameResolver).
    private String createdByName;
    private String updatedByName;
}
