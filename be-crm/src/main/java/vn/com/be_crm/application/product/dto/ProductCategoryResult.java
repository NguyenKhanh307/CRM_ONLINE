package vn.com.be_crm.application.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Output DTO của ProductCategory UseCase. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class ProductCategoryResult {
    private Long id;
    private String code;
    private String name;
    private Long parentId;
    private Integer sortOrder;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
