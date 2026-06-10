package vn.com.be_crm.application.customer.dto;

/** Một dòng dữ liệu Customer từ file import. */
public record ImportCustomerRowCommand(
        String name,
        String type,
        String taxCode,
        String phone,
        String email,
        String address,
        String source,
        String status,
        Long ownerId,
        String ownerEmail
) {}
