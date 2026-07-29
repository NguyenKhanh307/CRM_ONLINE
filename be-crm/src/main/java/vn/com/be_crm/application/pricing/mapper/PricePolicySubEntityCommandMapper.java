package vn.com.be_crm.application.pricing.mapper;

import vn.com.be_crm.application.pricing.dto.*;
import vn.com.be_crm.domain.pricing.entity.*;

/**
 * Mapper cho các sub-entity đơn giản của PricePolicy
 * (PricePolicyCustomer, PricePolicyProductCategory, PricePolicyEmployee).
 */
public class PricePolicySubEntityCommandMapper {

    /** Tạo PricePolicyCustomer. @param cmd @return domain entity */
    public static PricePolicyCustomer toCustomerEntity(CreatePricePolicyCustomerCommand cmd) {
        return PricePolicyCustomer.builder().pricePolicyId(cmd.getPricePolicyId()).customerId(cmd.getCustomerId()).build();
    }
    /** Chuyển PricePolicyCustomer sang result. @param e @return result */
    public static PricePolicyCustomerResult toCustomerResult(PricePolicyCustomer e) {
        return PricePolicyCustomerResult.builder().id(e.getId()).pricePolicyId(e.getPricePolicyId()).customerId(e.getCustomerId()).build();
    }

    /** Tạo PricePolicyProductCategory. @param cmd @return domain entity */
    public static PricePolicyProductCategory toCategoryEntity(CreatePricePolicyProductCategoryCommand cmd) {
        return PricePolicyProductCategory.builder().pricePolicyId(cmd.getPricePolicyId()).categoryId(cmd.getCategoryId()).build();
    }
    /** Chuyển PricePolicyProductCategory sang result. @param e @return result */
    public static PricePolicyProductCategoryResult toCategoryResult(PricePolicyProductCategory e) {
        return PricePolicyProductCategoryResult.builder().id(e.getId()).pricePolicyId(e.getPricePolicyId()).categoryId(e.getCategoryId()).build();
    }

    /** Tạo PricePolicyEmployee. @param cmd @return domain entity */
    public static PricePolicyEmployee toEmployeeEntity(CreatePricePolicyEmployeeCommand cmd) {
        return PricePolicyEmployee.builder().pricePolicyId(cmd.getPricePolicyId()).userId(cmd.getUserId()).build();
    }
    /** Chuyển PricePolicyEmployee sang result. @param e @return result */
    public static PricePolicyEmployeeResult toEmployeeResult(PricePolicyEmployee e) {
        return PricePolicyEmployeeResult.builder().id(e.getId()).pricePolicyId(e.getPricePolicyId()).userId(e.getUserId()).build();
    }

    private PricePolicySubEntityCommandMapper() {}
}
