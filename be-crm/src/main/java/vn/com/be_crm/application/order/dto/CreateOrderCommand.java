package vn.com.be_crm.application.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.order.enums.OrderStatus;
import vn.com.be_crm.domain.order.enums.OrderType;
import vn.com.be_crm.domain.order.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Input DTO khi tạo mới đơn hàng. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateOrderCommand {
    @NotBlank(message = "Mã đơn hàng không được để trống") @Size(max = 20) private String code;
    private Long customerId;
    private Long contactId;
    private Long ownerId;
    private Long executorUnitId;
    private Long warehouseId;
    private Long parentOrderId;
    private OrderType orderType;
    private LocalDate orderDate;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal total;
    @Size(max = 255) private String note;
}
