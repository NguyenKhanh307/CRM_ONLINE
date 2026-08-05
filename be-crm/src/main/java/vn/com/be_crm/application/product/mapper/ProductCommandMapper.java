package vn.com.be_crm.application.product.mapper;

import vn.com.be_crm.application.product.dto.*;
import vn.com.be_crm.domain.product.entity.Product;
import vn.com.be_crm.domain.product.enums.ProductStatus;
import vn.com.be_crm.domain.product.enums.ProductType;

import java.math.BigDecimal;

/** Chuyển đổi Command ↔ Product ↔ ProductResult. */
public class ProductCommandMapper {

    /**
     * Tạo Product từ CreateProductCommand.
     * @param cmd command tạo mới @return domain entity
     */
    public static Product toEntity(CreateProductCommand cmd) {
        return Product.builder()
                .sku(cmd.getSku()).name(cmd.getName()).categoryId(cmd.getCategoryId())
                .type(cmd.getType() != null ? cmd.getType() : ProductType.goods)
                .unit(cmd.getUnit())
                .basePrice(cmd.getBasePrice() != null ? cmd.getBasePrice() : BigDecimal.ZERO)
                .costPrice(cmd.getCostPrice() != null ? cmd.getCostPrice() : BigDecimal.ZERO)
                .vatRate(cmd.getVatRate() != null ? cmd.getVatRate() : BigDecimal.ZERO)
                .description(cmd.getDescription())
                .status(cmd.getStatus() != null ? cmd.getStatus() : ProductStatus.active).build();
    }

    /**
     * Cập nhật Product từ UpdateProductCommand.
     * @param cmd command cập nhật @param e entity hiện tại @return domain entity đã cập nhật
     */
    public static Product toEntity(UpdateProductCommand cmd, Product e) {
        return Product.builder()
                .id(e.getId()).sku(e.getSku())
                .name(cmd.getName() != null ? cmd.getName() : e.getName())
                .categoryId(cmd.getCategoryId() != null ? cmd.getCategoryId() : e.getCategoryId())
                .type(cmd.getType() != null ? cmd.getType() : e.getType())
                .unit(cmd.getUnit() != null ? cmd.getUnit() : e.getUnit())
                .basePrice(cmd.getBasePrice() != null ? cmd.getBasePrice() : e.getBasePrice())
                .costPrice(cmd.getCostPrice() != null ? cmd.getCostPrice() : e.getCostPrice())
                .vatRate(cmd.getVatRate() != null ? cmd.getVatRate() : e.getVatRate())
                .description(cmd.getDescription() != null ? cmd.getDescription() : e.getDescription())
                .status(cmd.getStatus() != null ? cmd.getStatus() : e.getStatus())
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .createdAt(e.getCreatedAt()).build();
    }

    /**
     * Chuyển Product sang ProductResult.
     * @param e domain entity @return result DTO
     */
    public static ProductResult toResult(Product e) {
        return ProductResult.builder()
                .id(e.getId()).sku(e.getSku()).name(e.getName()).categoryId(e.getCategoryId())
                .type(e.getType()).unit(e.getUnit())
                .basePrice(e.getBasePrice())
                .costPrice(e.getCostPrice()).vatRate(e.getVatRate())
                .description(e.getDescription()).status(e.getStatus())
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }

    private ProductCommandMapper() {}
}
