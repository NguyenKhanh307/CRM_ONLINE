package vn.com.be_crm.application.order.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.order.enums.DeliveryStatus;

import java.time.LocalDateTime;

/** Input DTO khi cập nhật theo dõi giao hàng. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateOrderDeliveryTrackingCommand {
    private Long id;
    private DeliveryStatus status;
    @Size(max = 100) private String carrier;
    @Size(max = 100) private String trackingNo;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    @Size(max = 500) private String note;
}
