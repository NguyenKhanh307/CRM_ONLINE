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
    /** ID giai đoạn. */
    private Long id;
    /** Tên giai đoạn. */
    private String name;
    /** Thứ tự trong pipeline. */
    private Integer sortOrder;
    /** Xác suất thắng (%). */
    private BigDecimal probability;
    /** true nếu là giai đoạn thắng. */
    private Boolean isWon;
    /** true nếu là giai đoạn thua. */
    private Boolean isLost;
    /** Thời điểm tạo. */
    private LocalDateTime createdAt;
    /** Thời điểm cập nhật gần nhất. */
    private LocalDateTime updatedAt;
}
