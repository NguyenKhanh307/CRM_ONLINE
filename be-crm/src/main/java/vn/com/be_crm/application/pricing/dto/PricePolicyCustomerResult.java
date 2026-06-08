package vn.com.be_crm.application.pricing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Output DTO cho PricePolicyCustomer. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class PricePolicyCustomerResult {
    private Long id;
    private Long pricePolicyId;
    private Long customerId;
}
