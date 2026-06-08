package vn.com.be_crm.domain.pricing.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.pricing.enums.PricePolicyStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Domain entity đại diện cho chính sách giá.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricePolicy {
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
