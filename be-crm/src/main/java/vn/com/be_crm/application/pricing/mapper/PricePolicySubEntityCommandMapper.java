package vn.com.be_crm.application.pricing.mapper;

import vn.com.be_crm.application.pricing.dto.*;
import vn.com.be_crm.domain.pricing.entity.*;

// mapper cho các sub-entity đơn giản của PricePolicy (PricePolicyCustomer, PricePolicyProductCategory)
public class PricePolicySubEntityCommandMapper {

    // tạo PricePolicyCustomer
    public static PricePolicyCustomer toCustomerEntity(CreatePricePolicyCustomerCommand cmd) {
        return PricePolicyCustomer.builder().pricePolicyId(cmd.getPricePolicyId()).customerId(cmd.getCustomerId()).build();
    }
    // chuyển PricePolicyCustomer sang result
    public static PricePolicyCustomerResult toCustomerResult(PricePolicyCustomer e) {
        return PricePolicyCustomerResult.builder().id(e.getId()).pricePolicyId(e.getPricePolicyId()).customerId(e.getCustomerId()).build();
    }

    // tạo PricePolicyProductCategory
    public static PricePolicyProductCategory toCategoryEntity(CreatePricePolicyProductCategoryCommand cmd) {
        return PricePolicyProductCategory.builder().pricePolicyId(cmd.getPricePolicyId()).categoryId(cmd.getCategoryId()).build();
    }
    // chuyển PricePolicyProductCategory sang result
    public static PricePolicyProductCategoryResult toCategoryResult(PricePolicyProductCategory e) {
        return PricePolicyProductCategoryResult.builder().id(e.getId()).pricePolicyId(e.getPricePolicyId()).categoryId(e.getCategoryId()).build();
    }

    private PricePolicySubEntityCommandMapper() {}
}
