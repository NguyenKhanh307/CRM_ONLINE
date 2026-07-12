package vn.com.be_crm.application.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Output DTO của OrgUnit UseCase — không expose domain entity ra ngoài.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrgUnitResult {
    private Long id;
    private String code;
    private String name;
    private Long parentId;

    /** Trưởng đơn vị — nhận thông báo thay cho nhân viên trong đơn vị. */
    private Long managerId;
    private Integer level;
    private String path;
    private Integer sortOrder;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
