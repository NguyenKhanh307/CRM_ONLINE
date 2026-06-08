package vn.com.be_crm.application.auth.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Input DTO khi cập nhật đơn vị tổ chức.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrgUnitCommand {

    @NotNull(message = "ID không được để trống")
    private Long id;

    @Size(max = 50, message = "Tên đơn vị tối đa 50 ký tự")
    private String name;

    private Long parentId;
    private Integer level;
    private String path;
    private Integer sortOrder;
    private Boolean isActive;
}
