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
                .salutation(cmd.getSalutation())
                .fullName(cmd.getFullName()).title(cmd.getTitle()).department(cmd.getDepartment())
                .email(cmd.getEmail())
                .zalo(cmd.getZalo()).phone(cmd.getPhone()).source(cmd.getSource())
                .gender(cmd.getGender()).dateOfBirth(cmd.getDateOfBirth())
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
                .salutation(cmd.getSalutation() != null ? cmd.getSalutation() : e.getSalutation())
                .fullName(cmd.getFullName() != null ? cmd.getFullName() : e.getFullName())
                .title(cmd.getTitle() != null ? cmd.getTitle() : e.getTitle())
                .department(cmd.getDepartment() != null ? cmd.getDepartment() : e.getDepartment())
                .email(cmd.getEmail() != null ? cmd.getEmail() : e.getEmail())
                .zalo(cmd.getZalo() != null ? cmd.getZalo() : e.getZalo())
                .phone(cmd.getPhone() != null ? cmd.getPhone() : e.getPhone())
                .source(cmd.getSource() != null ? cmd.getSource() : e.getSource())
                .gender(cmd.getGender() != null ? cmd.getGender() : e.getGender())
                .dateOfBirth(cmd.getDateOfBirth() != null ? cmd.getDateOfBirth() : e.getDateOfBirth())
                .isPrimary(cmd.getIsPrimary() != null ? cmd.getIsPrimary() : e.getIsPrimary())
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .createdAt(e.getCreatedAt()).build();
    }

    /**
     * Chuyển Contact sang ContactResult.
     * @param e domain entity @return result DTO
     */
    public static ContactResult toResult(Contact e) {
        return ContactResult.builder()
                .id(e.getId()).customerId(e.getCustomerId()).assignedUserId(e.getAssignedUserId())
                .salutation(e.getSalutation())
                .fullName(e.getFullName()).title(e.getTitle()).department(e.getDepartment())
                .email(e.getEmail())
                .zalo(e.getZalo()).phone(e.getPhone()).source(e.getSource())
                .gender(e.getGender()).dateOfBirth(e.getDateOfBirth())
                .isPrimary(e.getIsPrimary()).createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }

    private ContactCommandMapper() {}
}
