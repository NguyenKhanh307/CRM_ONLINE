package vn.com.be_crm.application.order.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// input khi cập nhật đơn hàng
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateOrderCommand {
    private Long id;
    private Long quotationId;
    private Long ownerId;
    private LocalDate orderDate;
    private LocalDate deliveryDate;
    @Size(max = 255) private String note;
}
