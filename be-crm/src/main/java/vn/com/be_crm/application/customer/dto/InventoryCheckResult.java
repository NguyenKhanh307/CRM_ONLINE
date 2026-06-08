package vn.com.be_crm.application.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.customer.enums.InventoryCheckStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Output DTO cho InventoryCheck. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class InventoryCheckResult {
    private Long id;
    private String code;
    private Long customerId;
    private Long checkedBy;
    private LocalDate checkDate;
    private InventoryCheckStatus status;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
