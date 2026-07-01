package vn.com.be_crm.application.service.mapper;

import vn.com.be_crm.application.service.dto.SlaPolicyResult;
import vn.com.be_crm.domain.service.entity.SlaPolicy;

/** Chuyển đổi SlaPolicy ↔ SlaPolicyResult. */
public class SlaPolicyMapper {

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
