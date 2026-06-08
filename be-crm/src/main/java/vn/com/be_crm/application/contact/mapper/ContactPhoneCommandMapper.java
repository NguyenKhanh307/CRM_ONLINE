package vn.com.be_crm.application.contact.mapper;

import vn.com.be_crm.application.contact.dto.*;
import vn.com.be_crm.domain.contact.entity.ContactPhone;
import vn.com.be_crm.domain.contact.enums.PhoneType;

/** Chuyển đổi Command ↔ ContactPhone ↔ ContactPhoneResult. */
public class ContactPhoneCommandMapper {

    /**
     * Tạo ContactPhone từ CreateContactPhoneCommand.
     * @param cmd command tạo mới @return domain entity
     */
    public static ContactPhone toEntity(CreateContactPhoneCommand cmd) {
        return ContactPhone.builder()
                .contactId(cmd.getContactId()).phone(cmd.getPhone())
                .phoneType(cmd.getPhoneType() != null ? cmd.getPhoneType() : PhoneType.mobile)
                .isPrimary(cmd.getIsPrimary() != null ? cmd.getIsPrimary() : false).build();
    }

    /**
     * Cập nhật ContactPhone từ UpdateContactPhoneCommand.
     * @param cmd command cập nhật @param e entity hiện tại @return domain entity đã cập nhật
     */
    public static ContactPhone toEntity(UpdateContactPhoneCommand cmd, ContactPhone e) {
        return ContactPhone.builder()
                .id(e.getId()).contactId(e.getContactId())
                .phone(cmd.getPhone() != null ? cmd.getPhone() : e.getPhone())
                .phoneType(cmd.getPhoneType() != null ? cmd.getPhoneType() : e.getPhoneType())
                .isPrimary(cmd.getIsPrimary() != null ? cmd.getIsPrimary() : e.getIsPrimary()).build();
    }

    /**
     * Chuyển ContactPhone sang ContactPhoneResult.
     * @param e domain entity @return result DTO
     */
    public static ContactPhoneResult toResult(ContactPhone e) {
        return ContactPhoneResult.builder()
                .id(e.getId()).contactId(e.getContactId()).phone(e.getPhone())
                .phoneType(e.getPhoneType()).isPrimary(e.getIsPrimary()).createdAt(e.getCreatedAt()).build();
    }

    private ContactPhoneCommandMapper() {}
}
