package vn.com.be_crm.application.customer.dto;

import java.math.BigDecimal;

/** Một dòng dữ liệu Customer từ file import. */
public record ImportCustomerRowCommand(
        String name,
        String shortName,
        String type,
        String taxCode,
        String phone,
        String email,
        String website,
        String address,
        String industry,
        String source,
        String status,
        Integer creditDays,
        BigDecimal creditLimit,
        String rating,
        String employeeSize,
        Boolean isDistributor,
        Long ownerId,
        String ownerEmail
) {}
