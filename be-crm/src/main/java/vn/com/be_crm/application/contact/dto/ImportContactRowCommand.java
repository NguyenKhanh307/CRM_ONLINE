package vn.com.be_crm.application.contact.dto;

/** Một dòng dữ liệu Contact từ file import. */
public record ImportContactRowCommand(
        String fullName,
        String position,
        String email,
        String gender,
        String dateOfBirth,
        String address,
        Long ownerId,
        String ownerEmail
) {}
