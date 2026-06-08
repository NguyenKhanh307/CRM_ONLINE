package vn.com.be_crm.domain.customer.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Domain entity đại diện cho dòng hàng trong phiếu kiểm kho.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryCheckItem {
    private Long id;
    private Long checkId;
    private Long productId;
    private BigDecimal quantity;
    private String note;
}
