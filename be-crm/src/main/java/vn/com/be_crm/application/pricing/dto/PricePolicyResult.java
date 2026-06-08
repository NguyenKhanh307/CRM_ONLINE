package vn.com.be_crm.application.pricing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.pricing.enums.PricePolicyStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Output DTO cho PricePolicy. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class PricePolicyResult {
    private Long id;
    private String code;
    private String name;
    private String type;
    private Integer priority;
    private LocalDate startDate;
    private LocalDate endDate;
    private PricePolicyStatus status;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
