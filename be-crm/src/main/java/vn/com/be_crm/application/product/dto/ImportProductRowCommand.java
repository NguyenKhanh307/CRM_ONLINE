package vn.com.be_crm.application.product.dto;

import java.math.BigDecimal;

/** Một dòng dữ liệu Product từ file import. */
public record ImportProductRowCommand(
        String name,
        String sku,
        Long categoryId,
        String type,
        String unit,
        BigDecimal basePrice,
        BigDecimal costPrice,
        BigDecimal vatRate,
        String description,
        String status
) {}
