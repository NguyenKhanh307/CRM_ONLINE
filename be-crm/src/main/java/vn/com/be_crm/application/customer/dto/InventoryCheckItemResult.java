package vn.com.be_crm.application.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Output DTO cho InventoryCheckItem. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class InventoryCheckItemResult {
    private Long id;
    private Long checkId;
    private Long productId;
    private BigDecimal quantity;
    private String note;
}
