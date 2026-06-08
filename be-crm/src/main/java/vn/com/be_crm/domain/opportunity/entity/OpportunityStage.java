package vn.com.be_crm.domain.opportunity.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain entity đại diện cho giai đoạn pipeline cơ hội bán hàng.
 */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class OpportunityStage {
    private Long id;
    private String name;
    private Integer sortOrder;
    /** Xác suất thắng (%). */
    private BigDecimal probability;
    /** true nếu là giai đoạn thắng. */
    private Boolean isWon;
    /** true nếu là giai đoạn thua. */
    private Boolean isLost;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
