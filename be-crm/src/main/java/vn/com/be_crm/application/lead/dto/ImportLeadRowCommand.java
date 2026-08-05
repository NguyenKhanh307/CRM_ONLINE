package vn.com.be_crm.application.lead.dto;

// một dòng dữ liệu Lead từ file import
public record ImportLeadRowCommand(
        String name,
        String companyName,
        String leadType,
        String taxCode,
        String website,
        String industry,
        String phone,
        String email,
        String source,
        Long campaignId,
        String status,
        String note,
        Long ownerId,
        String ownerEmail
) {}
