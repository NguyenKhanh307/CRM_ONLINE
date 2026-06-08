package vn.com.be_crm.presentation.auth.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * JSON input khi cập nhật đơn vị tổ chức.
 */
@Getter
@NoArgsConstructor
public class UpdateOrgUnitRequest {

    @Size(max = 50)
    private String name;

    private Long parentId;
    private Integer level;
    private String path;
    private Integer sortOrder;
    private Boolean isActive;
}
