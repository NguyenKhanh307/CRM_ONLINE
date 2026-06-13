package vn.com.be_crm.application.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    @Size(max = 20) private String secondaryUnit;
    private BigDecimal conversionRate;
    @Size(max = 100) private String composition;
    @Size(max = 30) private String yarnCount;
    @Size(max = 50) private String color;
    private BigDecimal fabricWidth;
    private BigDecimal weightGsm;
    @Size(max = 100) private String brand;
    @Size(max = 100) private String origin;
    private BigDecimal basePrice;
    private BigDecimal costPrice;
    private BigDecimal vatRate;
    private String barcode;
    private String description;
    private Boolean isDiscontinued;
    private Boolean isActive;
}
