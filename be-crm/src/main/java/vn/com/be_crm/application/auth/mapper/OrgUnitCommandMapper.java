package vn.com.be_crm.application.auth.mapper;

import vn.com.be_crm.application.auth.dto.CreateOrgUnitCommand;
import vn.com.be_crm.application.auth.dto.OrgUnitResult;
import vn.com.be_crm.application.auth.dto.UpdateOrgUnitCommand;
import vn.com.be_crm.domain.auth.entity.OrgUnit;

/**
 * Chuyển đổi giữa Command ↔ OrgUnit domain entity ↔ OrgUnitResult.
 */
public class OrgUnitCommandMapper {

    /**
     * Tạo OrgUnit domain entity từ CreateOrgUnitCommand.
     *
     * @param cmd command tạo mới
     * @return OrgUnit domain entity chưa có ID
     */
    public static OrgUnit toEntity(CreateOrgUnitCommand cmd) {
        return OrgUnit.builder()
                .code(cmd.getCode())
                .name(cmd.getName())
                .parentId(cmd.getParentId())
                .level(cmd.getLevel() != null ? cmd.getLevel() : 1)
                .path(cmd.getPath())
                .sortOrder(cmd.getSortOrder() != null ? cmd.getSortOrder() : 0)
                .isActive(cmd.getIsActive() != null ? cmd.getIsActive() : true)
                .build();
    }

    /**
     * Cập nhật OrgUnit domain entity từ UpdateOrgUnitCommand.
     *
     * @param cmd      command cập nhật
     * @param existing entity hiện tại
     * @return OrgUnit đã cập nhật
     */
    public static OrgUnit toEntity(UpdateOrgUnitCommand cmd, OrgUnit existing) {
        return OrgUnit.builder()
                .id(existing.getId())
                .code(existing.getCode())
                .name(cmd.getName() != null ? cmd.getName() : existing.getName())
                .parentId(cmd.getParentId() != null ? cmd.getParentId() : existing.getParentId())
                .level(cmd.getLevel() != null ? cmd.getLevel() : existing.getLevel())
                .path(cmd.getPath() != null ? cmd.getPath() : existing.getPath())
                .sortOrder(cmd.getSortOrder() != null ? cmd.getSortOrder() : existing.getSortOrder())
                .isActive(cmd.getIsActive() != null ? cmd.getIsActive() : existing.getIsActive())
                .createdAt(existing.getCreatedAt())
                .build();
    }

    /**
     * Chuyển OrgUnit domain entity sang OrgUnitResult output DTO.
     *
     * @param entity domain entity
     * @return OrgUnitResult
     */
    public static OrgUnitResult toResult(OrgUnit entity) {
        return OrgUnitResult.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .parentId(entity.getParentId())
                .level(entity.getLevel())
                .path(entity.getPath())
                .sortOrder(entity.getSortOrder())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private OrgUnitCommandMapper() {}
}
