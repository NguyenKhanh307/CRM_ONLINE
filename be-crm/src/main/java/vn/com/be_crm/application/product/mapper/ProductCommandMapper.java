package vn.com.be_crm.application.product.mapper;

import vn.com.be_crm.application.product.dto.*;
import vn.com.be_crm.domain.product.entity.Product;
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
                .unit(cmd.getUnit()).secondaryUnit(cmd.getSecondaryUnit())
                .conversionRate(cmd.getConversionRate()).composition(cmd.getComposition())
                .yarnCount(cmd.getYarnCount()).color(cmd.getColor())
                .fabricWidth(cmd.getFabricWidth()).weightGsm(cmd.getWeightGsm())
                .brand(cmd.getBrand()).origin(cmd.getOrigin())
                .basePrice(cmd.getBasePrice() != null ? cmd.getBasePrice() : BigDecimal.ZERO)
                .costPrice(cmd.getCostPrice() != null ? cmd.getCostPrice() : BigDecimal.ZERO)
                .vatRate(cmd.getVatRate() != null ? cmd.getVatRate() : BigDecimal.ZERO)
                .barcode(cmd.getBarcode()).description(cmd.getDescription())
                .isDiscontinued(cmd.getIsDiscontinued() != null ? cmd.getIsDiscontinued() : false)
                .isActive(cmd.getIsActive() != null ? cmd.getIsActive() : true).build();
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
                .secondaryUnit(cmd.getSecondaryUnit() != null ? cmd.getSecondaryUnit() : e.getSecondaryUnit())
                .conversionRate(cmd.getConversionRate() != null ? cmd.getConversionRate() : e.getConversionRate())
                .composition(cmd.getComposition() != null ? cmd.getComposition() : e.getComposition())
                .yarnCount(cmd.getYarnCount() != null ? cmd.getYarnCount() : e.getYarnCount())
                .color(cmd.getColor() != null ? cmd.getColor() : e.getColor())
                .fabricWidth(cmd.getFabricWidth() != null ? cmd.getFabricWidth() : e.getFabricWidth())
                .weightGsm(cmd.getWeightGsm() != null ? cmd.getWeightGsm() : e.getWeightGsm())
                .brand(cmd.getBrand() != null ? cmd.getBrand() : e.getBrand())
                .origin(cmd.getOrigin() != null ? cmd.getOrigin() : e.getOrigin())
                .basePrice(cmd.getBasePrice() != null ? cmd.getBasePrice() : e.getBasePrice())
                .costPrice(cmd.getCostPrice() != null ? cmd.getCostPrice() : e.getCostPrice())
                .vatRate(cmd.getVatRate() != null ? cmd.getVatRate() : e.getVatRate())
                .barcode(cmd.getBarcode() != null ? cmd.getBarcode() : e.getBarcode())
                .description(cmd.getDescription() != null ? cmd.getDescription() : e.getDescription())
                .isDiscontinued(cmd.getIsDiscontinued() != null ? cmd.getIsDiscontinued() : e.getIsDiscontinued())
                .isActive(cmd.getIsActive() != null ? cmd.getIsActive() : e.getIsActive())
                .createdAt(e.getCreatedAt()).build();
    }

    /**
     * Chuyển Product sang ProductResult.
     * @param e domain entity @return result DTO
     */
    public static ProductResult toResult(Product e) {
        return ProductResult.builder()
                .id(e.getId()).sku(e.getSku()).name(e.getName()).categoryId(e.getCategoryId())
                .type(e.getType()).unit(e.getUnit()).secondaryUnit(e.getSecondaryUnit())
                .conversionRate(e.getConversionRate()).composition(e.getComposition())
                .yarnCount(e.getYarnCount()).color(e.getColor())
                .fabricWidth(e.getFabricWidth()).weightGsm(e.getWeightGsm())
                .brand(e.getBrand()).origin(e.getOrigin())
                .basePrice(e.getBasePrice())
                .costPrice(e.getCostPrice()).vatRate(e.getVatRate()).barcode(e.getBarcode())
                .description(e.getDescription()).isDiscontinued(e.getIsDiscontinued())
                .isActive(e.getIsActive()).createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }

    private ProductCommandMapper() {}
}
