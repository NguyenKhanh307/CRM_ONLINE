package vn.com.be_crm.application.contact.dto;

/** Một dòng dữ liệu Contact từ file import. */
public record ImportContactRowCommand(
        Long customerId,
        String salutation,
        String fullName,
        String title,
        String department,
        String position,
        String email,
        String workEmail,
        String personalEmail,
        String zalo,
        String source,
        String gender,
        String dateOfBirth,
        String address,
        Boolean doNotCall,
        Boolean doNotEmail,
        Boolean isPrimary,
        Long ownerId,
        String ownerEmail
) {}
