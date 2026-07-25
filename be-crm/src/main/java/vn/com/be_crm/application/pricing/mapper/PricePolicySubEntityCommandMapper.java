package vn.com.be_crm.application.pricing.mapper;

import vn.com.be_crm.application.pricing.dto.*;
import vn.com.be_crm.domain.pricing.entity.*;

/**
 * Mapper cho các sub-entity đơn giản của PricePolicy
 * (PricePolicyCustomer, PricePolicyCustomerCategory, PricePolicyProductType, PricePolicyEmployee).
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

    /** Tạo PricePolicyCustomerCategory. @param cmd @return domain entity */
    public static PricePolicyCustomerCategory toCategoryEntity(CreatePricePolicyCustomerCategoryCommand cmd) {
        return PricePolicyCustomerCategory.builder().pricePolicyId(cmd.getPricePolicyId()).categoryId(cmd.getCategoryId()).build();
    }
    /** Chuyển PricePolicyCustomerCategory sang result. @param e @return result */
    public static PricePolicyCustomerCategoryResult toCategoryResult(PricePolicyCustomerCategory e) {
        return PricePolicyCustomerCategoryResult.builder().id(e.getId()).pricePolicyId(e.getPricePolicyId()).categoryId(e.getCategoryId()).build();
    }

    /** Tạo PricePolicyProductType. @param cmd @return domain entity */
    public static PricePolicyProductType toProductTypeEntity(CreatePricePolicyProductTypeCommand cmd) {
        return PricePolicyProductType.builder().pricePolicyId(cmd.getPricePolicyId()).productTypeId(cmd.getProductTypeId()).build();
    }
    /** Chuyển PricePolicyProductType sang result. @param e @return result */
    public static PricePolicyProductTypeResult toProductTypeResult(PricePolicyProductType e) {
        return PricePolicyProductTypeResult.builder().id(e.getId()).pricePolicyId(e.getPricePolicyId()).productTypeId(e.getProductTypeId()).build();
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
