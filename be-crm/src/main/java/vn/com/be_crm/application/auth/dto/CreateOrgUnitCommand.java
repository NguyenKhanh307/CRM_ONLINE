package vn.com.be_crm.application.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Input DTO khi tạo mới đơn vị tổ chức.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrgUnitCommand {

    @NotBlank(message = "Mã đơn vị không được để trống")
    @Size(max = 20, message = "Mã đơn vị tối đa 20 ký tự")
    private String code;

    @NotBlank(message = "Tên đơn vị không được để trống")
    @Size(max = 50, message = "Tên đơn vị tối đa 50 ký tự")
    private String name;

    private Long parentId;
    private Integer level;
    private String path;
    private Integer sortOrder;
    private Boolean isActive;
}
