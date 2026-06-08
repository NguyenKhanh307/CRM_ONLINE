package vn.com.be_crm.application.pricing.mapper;

import vn.com.be_crm.application.pricing.dto.*;
import vn.com.be_crm.domain.pricing.entity.PricePolicy;
import vn.com.be_crm.domain.pricing.enums.PricePolicyStatus;

/** Chuyển đổi Command ↔ PricePolicy ↔ PricePolicyResult. */
public class PricePolicyCommandMapper {

    /**
     * Tạo PricePolicy từ CreatePricePolicyCommand.
     * @param cmd command tạo mới @return domain entity
     */
    public static PricePolicy toEntity(CreatePricePolicyCommand cmd) {
        return PricePolicy.builder()
                .code(cmd.getCode()).name(cmd.getName()).type(cmd.getType())
                .priority(cmd.getPriority() != null ? cmd.getPriority() : 0)
                .startDate(cmd.getStartDate()).endDate(cmd.getEndDate())
                .status(cmd.getStatus() != null ? cmd.getStatus() : PricePolicyStatus.active)
                .createdBy(cmd.getCreatedBy()).build();
    }

    /**
     * Cập nhật PricePolicy từ UpdatePricePolicyCommand.
     * @param cmd command cập nhật @param e entity hiện tại @return domain entity đã cập nhật
     */
    public static PricePolicy toEntity(UpdatePricePolicyCommand cmd, PricePolicy e) {
        return PricePolicy.builder()
                .id(e.getId()).code(e.getCode())
                .name(cmd.getName() != null ? cmd.getName() : e.getName())
                .type(cmd.getType() != null ? cmd.getType() : e.getType())
                .priority(cmd.getPriority() != null ? cmd.getPriority() : e.getPriority())
                .startDate(cmd.getStartDate() != null ? cmd.getStartDate() : e.getStartDate())
                .endDate(cmd.getEndDate() != null ? cmd.getEndDate() : e.getEndDate())
                .status(cmd.getStatus() != null ? cmd.getStatus() : e.getStatus())
                .createdBy(e.getCreatedBy()).createdAt(e.getCreatedAt()).build();
    }

    /**
     * Chuyển PricePolicy sang PricePolicyResult.
     * @param e domain entity @return result DTO
     */
    public static PricePolicyResult toResult(PricePolicy e) {
        return PricePolicyResult.builder()
                .id(e.getId()).code(e.getCode()).name(e.getName()).type(e.getType())
                .priority(e.getPriority()).startDate(e.getStartDate()).endDate(e.getEndDate())
                .status(e.getStatus()).createdBy(e.getCreatedBy())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }

    private PricePolicyCommandMapper() {}
}
