package vn.com.be_crm.application.pricing.mapper;

import vn.com.be_crm.application.pricing.dto.*;
import vn.com.be_crm.domain.pricing.entity.PricePolicyProduct;

import java.math.BigDecimal;

/** Chuyển đổi Command ↔ PricePolicyProduct ↔ PricePolicyProductResult. */
public class PricePolicyProductCommandMapper {

    /**
     * Tạo PricePolicyProduct từ CreatePricePolicyProductCommand.
     * @param cmd command tạo mới @return domain entity
     */
    public static PricePolicyProduct toEntity(CreatePricePolicyProductCommand cmd) {
        return PricePolicyProduct.builder()
                .pricePolicyId(cmd.getPricePolicyId()).productId(cmd.getProductId())
                .price(cmd.getPrice()).discountType(cmd.getDiscountType())
                .discountValue(cmd.getDiscountValue() != null ? cmd.getDiscountValue() : BigDecimal.ZERO)
                .minQty(cmd.getMinQty() != null ? cmd.getMinQty() : BigDecimal.ZERO).build();
    }

    /**
     * Cập nhật PricePolicyProduct từ UpdatePricePolicyProductCommand.
     * @param cmd command cập nhật @param e entity hiện tại @return domain entity đã cập nhật
     */
    public static PricePolicyProduct toEntity(UpdatePricePolicyProductCommand cmd, PricePolicyProduct e) {
        return PricePolicyProduct.builder()
                .id(e.getId()).pricePolicyId(e.getPricePolicyId()).productId(e.getProductId())
                .price(cmd.getPrice() != null ? cmd.getPrice() : e.getPrice())
                .discountType(cmd.getDiscountType() != null ? cmd.getDiscountType() : e.getDiscountType())
                .discountValue(cmd.getDiscountValue() != null ? cmd.getDiscountValue() : e.getDiscountValue())
                .minQty(cmd.getMinQty() != null ? cmd.getMinQty() : e.getMinQty()).build();
    }

    /**
     * Chuyển PricePolicyProduct sang PricePolicyProductResult.
     * @param e domain entity @return result DTO
     */
    public static PricePolicyProductResult toResult(PricePolicyProduct e) {
        return PricePolicyProductResult.builder()
                .id(e.getId()).pricePolicyId(e.getPricePolicyId()).productId(e.getProductId())
                .price(e.getPrice()).discountType(e.getDiscountType()).discountValue(e.getDiscountValue())
                .minQty(e.getMinQty()).build();
    }

    private PricePolicyProductCommandMapper() {}
}
