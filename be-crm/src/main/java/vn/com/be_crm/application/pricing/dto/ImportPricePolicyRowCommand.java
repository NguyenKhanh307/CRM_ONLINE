package vn.com.be_crm.application.pricing.dto;

/** Một dòng dữ liệu PricePolicy từ file import. */
public record ImportPricePolicyRowCommand(
        String code,
        String name,
        String type,
        Integer priority,
        String startDate,
        String endDate,
        String status
) {}
