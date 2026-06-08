package vn.com.be_crm.application.order.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.order.enums.OrderStatus;
import vn.com.be_crm.domain.order.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Input DTO khi cập nhật đơn hàng. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateOrderCommand {
    private Long id;
    private Long customerId;
    private Long contactId;
    private Long ownerId;
    private Long executorUnitId;
    private Long warehouseId;
    private LocalDate orderDate;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal total;
    @Size(max = 255) private String note;
}
