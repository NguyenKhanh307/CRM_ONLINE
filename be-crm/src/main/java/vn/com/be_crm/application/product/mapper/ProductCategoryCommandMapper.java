package vn.com.be_crm.application.product.mapper;

import vn.com.be_crm.application.product.dto.*;
import vn.com.be_crm.domain.product.entity.ProductCategory;

/** Chuyển đổi Command ↔ ProductCategory ↔ ProductCategoryResult. */
public class ProductCategoryCommandMapper {

    /**
     * Tạo ProductCategory từ CreateProductCategoryCommand.
     * @param cmd command tạo mới @return domain entity
     */
    public static ProductCategory toEntity(CreateProductCategoryCommand cmd) {
        return ProductCategory.builder()
                .code(cmd.getCode()).name(cmd.getName())
                .sortOrder(cmd.getSortOrder() != null ? cmd.getSortOrder() : 0)
                .isActive(cmd.getIsActive() != null ? cmd.getIsActive() : true).build();
    }

    /**
     * Cập nhật ProductCategory từ UpdateProductCategoryCommand.
     * @param cmd command cập nhật @param e entity hiện tại @return domain entity đã cập nhật
     */
    public static ProductCategory toEntity(UpdateProductCategoryCommand cmd, ProductCategory e) {
        return ProductCategory.builder()
                .id(e.getId()).code(e.getCode())
                .name(cmd.getName() != null ? cmd.getName() : e.getName())
                .sortOrder(cmd.getSortOrder() != null ? cmd.getSortOrder() : e.getSortOrder())
                .isActive(cmd.getIsActive() != null ? cmd.getIsActive() : e.getIsActive())
                .createdAt(e.getCreatedAt()).build();
    }

    /**
     * Chuyển ProductCategory sang ProductCategoryResult.
     * @param e domain entity @return result DTO
     */
    public static ProductCategoryResult toResult(ProductCategory e) {
        return ProductCategoryResult.builder()
                .id(e.getId()).code(e.getCode()).name(e.getName())
                .sortOrder(e.getSortOrder()).isActive(e.getIsActive())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }

    private ProductCategoryCommandMapper() {}
}
