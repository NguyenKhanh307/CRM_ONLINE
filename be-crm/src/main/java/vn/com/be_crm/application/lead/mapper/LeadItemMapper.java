package vn.com.be_crm.application.lead.mapper;

import vn.com.be_crm.application.lead.dto.CreateLeadItemCommand;
import vn.com.be_crm.application.lead.dto.LeadItemResult;
import vn.com.be_crm.domain.lead.entity.LeadItem;
import vn.com.be_crm.domain.lead.enums.LeadItemInterestType;

// chuyển đổi Command <-> LeadItem <-> LeadItemResult
public class LeadItemMapper {

    public static LeadItem toEntity(CreateLeadItemCommand cmd) {
        LeadItemInterestType type = cmd.getInterestType() != null && !cmd.getInterestType().isBlank()
                ? LeadItemInterestType.valueOf(cmd.getInterestType()) : LeadItemInterestType.viewed;
        return LeadItem.builder()
                .leadId(cmd.getLeadId()).productId(cmd.getProductId())
                .quantity(cmd.getQuantity()).interestType(type).build();
    }

    public static LeadItemResult toResult(LeadItem e) {
        return LeadItemResult.builder()
                .id(e.getId()).leadId(e.getLeadId()).productId(e.getProductId())
                .quantity(e.getQuantity()).interestType(e.getInterestType())
                .createdAt(e.getCreatedAt()).build();
    }

    private LeadItemMapper() {}
}
