package vn.com.be_crm.application.product.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.product.enums.ProductStatus;
import vn.com.be_crm.domain.product.enums.ProductType;

import java.math.BigDecimal;

/** Input DTO khi tạo mới hàng hóa. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateProductCommand {
    @NotBlank(message = "SKU không được để trống") @Size(max = 50) private String sku;
    @NotBlank(message = "Tên hàng hóa không được để trống") @Size(max = 255) private String name;
    private Long categoryId;
    @NotNull(message = "Loại hàng hóa không được để trống") private ProductType type;
    @Size(max = 20) private String unit;
    @PositiveOrZero(message = "Giá bán không được âm") private BigDecimal basePrice;
    @PositiveOrZero(message = "Giá vốn không được âm") private BigDecimal costPrice;
    @DecimalMin(value = "0", message = "Thuế VAT phải từ 0 đến 100") @DecimalMax(value = "100", message = "Thuế VAT phải từ 0 đến 100") private BigDecimal vatRate;
    private String description;
    private ProductStatus status;
}
