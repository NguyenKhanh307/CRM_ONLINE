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
                .position(cmd.getPosition()).email(cmd.getEmail())
                .workEmail(cmd.getWorkEmail()).personalEmail(cmd.getPersonalEmail())
                .zalo(cmd.getZalo()).source(cmd.getSource())
                .gender(cmd.getGender()).dateOfBirth(cmd.getDateOfBirth()).address(cmd.getAddress())
                .doNotCall(cmd.getDoNotCall() != null && cmd.getDoNotCall())
                .doNotEmail(cmd.getDoNotEmail() != null && cmd.getDoNotEmail())
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
                .position(cmd.getPosition() != null ? cmd.getPosition() : e.getPosition())
                .email(cmd.getEmail() != null ? cmd.getEmail() : e.getEmail())
                .workEmail(cmd.getWorkEmail() != null ? cmd.getWorkEmail() : e.getWorkEmail())
                .personalEmail(cmd.getPersonalEmail() != null ? cmd.getPersonalEmail() : e.getPersonalEmail())
                .zalo(cmd.getZalo() != null ? cmd.getZalo() : e.getZalo())
                .source(cmd.getSource() != null ? cmd.getSource() : e.getSource())
                .gender(cmd.getGender() != null ? cmd.getGender() : e.getGender())
                .dateOfBirth(cmd.getDateOfBirth() != null ? cmd.getDateOfBirth() : e.getDateOfBirth())
                .address(cmd.getAddress() != null ? cmd.getAddress() : e.getAddress())
                .doNotCall(cmd.getDoNotCall() != null ? cmd.getDoNotCall() : e.isDoNotCall())
                .doNotEmail(cmd.getDoNotEmail() != null ? cmd.getDoNotEmail() : e.isDoNotEmail())
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
                .position(e.getPosition()).email(e.getEmail())
                .workEmail(e.getWorkEmail()).personalEmail(e.getPersonalEmail())
                .zalo(e.getZalo()).source(e.getSource())
                .gender(e.getGender()).dateOfBirth(e.getDateOfBirth()).address(e.getAddress())
                .doNotCall(e.isDoNotCall()).doNotEmail(e.isDoNotEmail())
                .isPrimary(e.getIsPrimary()).createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }

    private ContactCommandMapper() {}
}
