package vn.com.be_crm.application.lead.dto;

import java.math.BigDecimal;

// một dòng dữ liệu Lead từ file import
public record ImportLeadRowCommand(
        String name,
        String companyName,
        String leadType,
        String title,
        String department,
        String taxCode,
        String website,
        String industry,
        String phone,
        String email,
        String source,
        Long campaignId,
        String status,
        BigDecimal estimatedValue,
        Boolean doNotCall,
        Boolean doNotEmail,
        String note,
        Long ownerId,
        String ownerEmail
) {}
