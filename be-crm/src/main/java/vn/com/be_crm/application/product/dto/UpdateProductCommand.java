package vn.com.be_crm.application.product.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.product.enums.ProductType;

import java.math.BigDecimal;

/** Input DTO khi cập nhật hàng hóa. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateProductCommand {
    private Long id;
    @Size(max = 255) private String name;
    private Long categoryId;
    private ProductType type;
    @Size(max = 20) private String unit;
    private BigDecimal basePrice;
    private BigDecimal costPrice;
    private BigDecimal vatRate;
    private String description;
    private Boolean isDiscontinued;
    private Boolean isActive;
}
