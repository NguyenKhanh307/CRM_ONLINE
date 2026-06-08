package vn.com.be_crm.application.order.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Input DTO khi cập nhật bản ghi doanh thu. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateOrderRevenueRecordCommand {
    private Long id;
    private BigDecimal revenueAmount;
    private BigDecimal percentage;
    @Size(max = 255) private String note;
}
