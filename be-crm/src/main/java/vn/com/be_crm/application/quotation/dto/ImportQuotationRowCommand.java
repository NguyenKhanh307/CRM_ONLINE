package vn.com.be_crm.application.quotation.dto;

// một dòng dữ liệu Quotation từ file import
public record ImportQuotationRowCommand(
        String code,
        Long customerId,
        Long contactId,
        Long opportunityId,
        Long pricePolicyId,
        String quoteDate,
        String validUntil,
        String status,
        String note,
        Long ownerId,
        String ownerEmail
) {}
