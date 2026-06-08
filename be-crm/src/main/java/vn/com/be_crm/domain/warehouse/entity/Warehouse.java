package vn.com.be_crm.domain.warehouse.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Domain entity đại diện cho kho hàng. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class Warehouse {
    private Long id;
    private String code;
    private String name;
    private String address;
    private Long managerId;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
