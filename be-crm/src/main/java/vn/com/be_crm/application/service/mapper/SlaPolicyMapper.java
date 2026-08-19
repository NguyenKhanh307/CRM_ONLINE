package vn.com.be_crm.application.service.mapper;

import vn.com.be_crm.application.service.dto.CreateSlaPolicyCommand;
import vn.com.be_crm.application.service.dto.SlaPolicyResult;
import vn.com.be_crm.application.service.dto.UpdateSlaPolicyCommand;
import vn.com.be_crm.domain.service.entity.SlaPolicy;

/** Chuyển đổi Command ↔ SlaPolicy ↔ SlaPolicyResult. */
public class SlaPolicyMapper {

    /** Tạo từ command tạo mới. @param c command @return entity */
    public static SlaPolicy toEntity(CreateSlaPolicyCommand c) {
        return SlaPolicy.builder()
                .code(c.getCode()).name(c.getName()).priority(c.getPriority())
                .firstResponseHours(c.getFirstResponseHours()).resolutionHours(c.getResolutionHours())
                .isActive(c.getIsActive() != null ? c.getIsActive() : true)
                .build();
    }

    /** Cập nhật từ command. @param c command @param e existing @return entity */
    public static SlaPolicy toEntity(UpdateSlaPolicyCommand c, SlaPolicy e) {
        return SlaPolicy.builder().id(e.getId()).code(e.getCode())
                .name(c.getName() != null ? c.getName() : e.getName())
                .priority(c.getPriority() != null ? c.getPriority() : e.getPriority())
                .firstResponseHours(c.getFirstResponseHours() != null ? c.getFirstResponseHours() : e.getFirstResponseHours())
                .resolutionHours(c.getResolutionHours() != null ? c.getResolutionHours() : e.getResolutionHours())
                .isActive(c.getIsActive() != null ? c.getIsActive() : e.isActive())
                .createdAt(e.getCreatedAt()).build();
    }

    /**
     * Chuyển SlaPolicy sang SlaPolicyResult.
     * @param e domain entity @return result DTO
     */
    public static SlaPolicyResult toResult(SlaPolicy e) {
        return SlaPolicyResult.builder()
                .id(e.getId()).code(e.getCode()).name(e.getName()).priority(e.getPriority())
                .firstResponseHours(e.getFirstResponseHours()).resolutionHours(e.getResolutionHours())
                .isActive(e.isActive())
                .build();
    }

    private SlaPolicyMapper() {}
}
