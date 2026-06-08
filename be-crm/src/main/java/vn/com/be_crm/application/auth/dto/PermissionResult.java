package vn.com.be_crm.application.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Output DTO của Permission UseCase.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResult {
    private Long id;
    private String code;
    private String name;
    private String module;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
