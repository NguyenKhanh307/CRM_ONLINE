package vn.com.be_crm.application.order.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

// input khi tạo mới đơn hàng (kèm dòng hàng nếu có)
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateOrderCommand {
    @NotBlank(message = "Mã đơn hàng không được để trống") @Size(max = 20) private String code;
    private Long quotationId;
    private Long ownerId;
    @FutureOrPresent(message = "Ngày đặt hàng không được là ngày quá khứ") private LocalDate orderDate;
    @FutureOrPresent(message = "Ngày giao hàng không được là ngày quá khứ") private LocalDate deliveryDate;
    @Size(max = 255) private String note;
    // dòng hàng tạo kèm đơn hàng (orderId bỏ trống)
    @Valid private List<CreateOrderItemCommand> items;
}
