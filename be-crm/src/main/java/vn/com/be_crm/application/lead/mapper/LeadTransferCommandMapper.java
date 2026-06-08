package vn.com.be_crm.application.lead.mapper;

import vn.com.be_crm.application.lead.dto.*;
import vn.com.be_crm.domain.lead.entity.LeadTransfer;

/** Chuyển đổi Command ↔ LeadTransfer ↔ LeadTransferResult. */
public class LeadTransferCommandMapper {

    /**
     * Tạo LeadTransfer từ CreateLeadTransferCommand.
     * @param cmd command tạo mới @return domain entity
     */
    public static LeadTransfer toEntity(CreateLeadTransferCommand cmd) {
        return LeadTransfer.builder()
                .leadId(cmd.getLeadId()).fromUserId(cmd.getFromUserId())
                .toUserId(cmd.getToUserId()).reason(cmd.getReason()).build();
    }

    /**
     * Cập nhật LeadTransfer từ UpdateLeadTransferCommand.
     * @param cmd command cập nhật @param e entity hiện tại @return domain entity đã cập nhật
     */
    public static LeadTransfer toEntity(UpdateLeadTransferCommand cmd, LeadTransfer e) {
        return LeadTransfer.builder()
                .id(e.getId()).leadId(e.getLeadId())
                .fromUserId(cmd.getFromUserId() != null ? cmd.getFromUserId() : e.getFromUserId())
                .toUserId(cmd.getToUserId() != null ? cmd.getToUserId() : e.getToUserId())
                .reason(cmd.getReason() != null ? cmd.getReason() : e.getReason())
                .transferredAt(e.getTransferredAt()).build();
    }

    /**
     * Chuyển LeadTransfer sang LeadTransferResult.
     * @param e domain entity @return result DTO
     */
    public static LeadTransferResult toResult(LeadTransfer e) {
        return LeadTransferResult.builder()
                .id(e.getId()).leadId(e.getLeadId()).fromUserId(e.getFromUserId())
                .toUserId(e.getToUserId()).reason(e.getReason()).transferredAt(e.getTransferredAt()).build();
    }

    private LeadTransferCommandMapper() {}
}
