package vn.com.be_crm.application.pricing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Output DTO cho PricePolicyEmployee. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class PricePolicyEmployeeResult {
    private Long id;
    private Long pricePolicyId;
    private Long userId;
}
