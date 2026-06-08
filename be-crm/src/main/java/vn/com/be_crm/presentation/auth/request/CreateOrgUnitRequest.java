package vn.com.be_crm.presentation.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * JSON input khi tạo mới đơn vị tổ chức.
 */
@Getter
@NoArgsConstructor
public class CreateOrgUnitRequest {

    @NotBlank(message = "Mã đơn vị không được để trống")
    @Size(max = 20)
    private String code;

    @NotBlank(message = "Tên đơn vị không được để trống")
    @Size(max = 50)
    private String name;

    private Long parentId;
    private Integer level;
    private String path;
    private Integer sortOrder;
    private Boolean isActive;
}
