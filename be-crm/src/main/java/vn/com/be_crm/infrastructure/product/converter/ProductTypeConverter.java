package vn.com.be_crm.infrastructure.product.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import vn.com.be_crm.domain.product.enums.ProductType;

/**
 * Ánh xạ ProductType ↔ giá trị ENUM tiếng Việt trong DB ('Vật tư hàng hóa','Dịch vụ').
 * Tương tự LeadStatusConverter — cho phép DB lưu nhãn tiếng Việt còn Java dùng enum.
 */
@Converter
public class ProductTypeConverter implements AttributeConverter<ProductType, String> {

    /** Tên hiển thị tiếng Việt cho từng loại. */
    private static final String GOODS = "Vật tư hàng hóa";
    private static final String SERVICE = "Dịch vụ";

    /** Enum → giá trị DB (tiếng Việt). @param type enum @return chuỗi ENUM DB */
    @Override
    public String convertToDatabaseColumn(ProductType type) {
        return type == ProductType.service ? SERVICE : GOODS;
    }

    /** Giá trị DB → enum (chấp nhận cả nhãn tiếng Việt lẫn tên enum). @param db chuỗi DB @return enum */
    @Override
    public ProductType convertToEntityAttribute(String db) {
        if (db == null) return ProductType.goods;
        String v = db.trim();
        if (v.equalsIgnoreCase(SERVICE) || v.equalsIgnoreCase("service")) return ProductType.service;
        return ProductType.goods;
    }
}
