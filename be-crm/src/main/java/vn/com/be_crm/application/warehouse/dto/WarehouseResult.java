package vn.com.be_crm.application.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Output DTO của Warehouse UseCase. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class WarehouseResult {
    private Long id;
    private String code;
    private String name;
    private String address;
    private Long managerId;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
