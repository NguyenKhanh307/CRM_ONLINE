package vn.com.be_crm.application.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Output DTO của Role UseCase.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResult {
    private Long id;
    private String code;
    private String name;
    private String description;
    private Boolean isSystem;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
