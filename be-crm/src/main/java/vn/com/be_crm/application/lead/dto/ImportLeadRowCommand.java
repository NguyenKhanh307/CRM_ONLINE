package vn.com.be_crm.application.lead.dto;

import java.math.BigDecimal;

/** Một dòng dữ liệu Lead từ file import. */
public record ImportLeadRowCommand(
        String name,
        String phone,
        String email,
        String source,
        String status,
        BigDecimal estimatedValue,
        String note,
        Long ownerId,
        String ownerEmail
) {}
