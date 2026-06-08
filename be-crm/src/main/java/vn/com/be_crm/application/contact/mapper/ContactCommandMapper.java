package vn.com.be_crm.application.contact.mapper;

import vn.com.be_crm.application.contact.dto.*;
import vn.com.be_crm.domain.contact.entity.Contact;

/** Chuyển đổi Command ↔ Contact ↔ ContactResult. */
public class ContactCommandMapper {

    /**
     * Tạo Contact từ CreateContactCommand.
     * @param cmd command tạo mới @return domain entity
     */
    public static Contact toEntity(CreateContactCommand cmd) {
        return Contact.builder()
                .customerId(cmd.getCustomerId()).assignedUserId(cmd.getAssignedUserId())
                .fullName(cmd.getFullName()).position(cmd.getPosition()).email(cmd.getEmail())
                .gender(cmd.getGender()).dateOfBirth(cmd.getDateOfBirth()).address(cmd.getAddress())
                .isPrimary(cmd.getIsPrimary() != null ? cmd.getIsPrimary() : false).build();
    }

    /**
     * Cập nhật Contact từ UpdateContactCommand.
     * @param cmd command cập nhật @param e entity hiện tại @return domain entity đã cập nhật
     */
    public static Contact toEntity(UpdateContactCommand cmd, Contact e) {
        return Contact.builder()
                .id(e.getId())
                .customerId(cmd.getCustomerId() != null ? cmd.getCustomerId() : e.getCustomerId())
                .assignedUserId(cmd.getAssignedUserId() != null ? cmd.getAssignedUserId() : e.getAssignedUserId())
                .fullName(cmd.getFullName() != null ? cmd.getFullName() : e.getFullName())
                .position(cmd.getPosition() != null ? cmd.getPosition() : e.getPosition())
                .email(cmd.getEmail() != null ? cmd.getEmail() : e.getEmail())
                .gender(cmd.getGender() != null ? cmd.getGender() : e.getGender())
                .dateOfBirth(cmd.getDateOfBirth() != null ? cmd.getDateOfBirth() : e.getDateOfBirth())
                .address(cmd.getAddress() != null ? cmd.getAddress() : e.getAddress())
                .isPrimary(cmd.getIsPrimary() != null ? cmd.getIsPrimary() : e.getIsPrimary())
                .createdAt(e.getCreatedAt()).build();
    }

    /**
     * Chuyển Contact sang ContactResult.
     * @param e domain entity @return result DTO
     */
    public static ContactResult toResult(Contact e) {
        return ContactResult.builder()
                .id(e.getId()).customerId(e.getCustomerId()).assignedUserId(e.getAssignedUserId())
                .fullName(e.getFullName()).position(e.getPosition()).email(e.getEmail())
                .gender(e.getGender()).dateOfBirth(e.getDateOfBirth()).address(e.getAddress())
                .isPrimary(e.getIsPrimary()).createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }

    private ContactCommandMapper() {}
}
