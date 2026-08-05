package vn.com.be_crm.infrastructure.pricing.mapper;

import org.springframework.stereotype.Component;
import vn.com.be_crm.domain.pricing.entity.*;
import vn.com.be_crm.infrastructure.pricing.entity.*;

import java.math.BigDecimal;

// mapper cho các sub-entity của PricePolicy (PricePolicyProduct, PricePolicyCustomer, PricePolicyProductCategory)
@Component
public class PricePolicySubEntityHibernateMapper {

    // chuyển PricePolicyProduct domain entity sang Hibernate entity
    public PricePolicyProductHibernate toProductHibernate(PricePolicyProduct d) {
        PricePolicyProductHibernate h = new PricePolicyProductHibernate();
        h.setId(d.getId()); h.setPricePolicyId(d.getPricePolicyId()); h.setProductId(d.getProductId());
        h.setPrice(d.getPrice()); h.setDiscountType(d.getDiscountType());
        h.setDiscountValue(d.getDiscountValue() != null ? d.getDiscountValue() : BigDecimal.ZERO);
        h.setMinQty(d.getMinQty() != null ? d.getMinQty() : BigDecimal.ZERO);
        return h;
    }
    // chuyển PricePolicyProductHibernate sang domain entity
    public PricePolicyProduct toProductDomain(PricePolicyProductHibernate h) {
        return PricePolicyProduct.builder().id(h.getId()).pricePolicyId(h.getPricePolicyId())
                .productId(h.getProductId()).price(h.getPrice()).discountType(h.getDiscountType())
                .discountValue(h.getDiscountValue()).minQty(h.getMinQty()).build();
    }

    // chuyển PricePolicyCustomer domain entity sang Hibernate entity
    public PricePolicyCustomerHibernate toCustomerHibernate(PricePolicyCustomer d) {
        PricePolicyCustomerHibernate h = new PricePolicyCustomerHibernate();
        h.setId(d.getId()); h.setPricePolicyId(d.getPricePolicyId()); h.setCustomerId(d.getCustomerId());
        return h;
    }
    // chuyển PricePolicyCustomerHibernate sang domain entity
    public PricePolicyCustomer toCustomerDomain(PricePolicyCustomerHibernate h) {
        return PricePolicyCustomer.builder().id(h.getId()).pricePolicyId(h.getPricePolicyId()).customerId(h.getCustomerId()).build();
    }

    // chuyển PricePolicyProductCategory domain entity sang Hibernate entity
    public PricePolicyProductCategoryHibernate toCategoryHibernate(PricePolicyProductCategory d) {
        PricePolicyProductCategoryHibernate h = new PricePolicyProductCategoryHibernate();
        h.setId(d.getId()); h.setPricePolicyId(d.getPricePolicyId()); h.setCategoryId(d.getCategoryId());
        return h;
    }
    // chuyển PricePolicyProductCategoryHibernate sang domain entity
    public PricePolicyProductCategory toCategoryDomain(PricePolicyProductCategoryHibernate h) {
        return PricePolicyProductCategory.builder().id(h.getId()).pricePolicyId(h.getPricePolicyId()).categoryId(h.getCategoryId()).build();
    }
}
