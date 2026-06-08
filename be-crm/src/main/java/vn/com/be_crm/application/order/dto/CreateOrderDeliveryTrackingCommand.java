package vn.com.be_crm.application.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.order.enums.DeliveryStatus;

import java.time.LocalDateTime;

/** Input DTO khi tạo mới theo dõi giao hàng. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateOrderDeliveryTrackingCommand {
    @NotNull private Long orderId;
    private DeliveryStatus status;
    @Size(max = 100) private String carrier;
    @Size(max = 100) private String trackingNo;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    @Size(max = 500) private String note;
}
