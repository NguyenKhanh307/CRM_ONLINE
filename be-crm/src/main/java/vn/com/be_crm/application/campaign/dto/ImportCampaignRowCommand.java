package vn.com.be_crm.application.campaign.dto;

import java.math.BigDecimal;

/** Một dòng dữ liệu Campaign từ file import. */
public record ImportCampaignRowCommand(
        String code,
        String name,
        String type,
        String status,
        String channel,
        String startDate,
        String endDate,
        BigDecimal budget,
        String note,
        Long ownerId,
        String ownerEmail
) {}
