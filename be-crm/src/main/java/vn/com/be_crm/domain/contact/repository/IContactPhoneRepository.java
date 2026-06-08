package vn.com.be_crm.domain.contact.repository;

import vn.com.be_crm.domain.contact.entity.ContactPhone;

import java.util.List;
import java.util.Optional;

/**
 * Port lưu trữ cho ContactPhone.
 */
public interface IContactPhoneRepository {

    /**
     * Lưu mới hoặc cập nhật số điện thoại.
     * @param phone domain entity @return entity sau khi lưu
     */
    ContactPhone save(ContactPhone phone);

    /**
     * Tìm số điện thoại theo ID.
     * @param id ID @return Optional
     */
    Optional<ContactPhone> findById(Long id);

    /**
     * Xóa số điện thoại theo ID.
     * @param id ID cần xóa
     */
    void deleteById(Long id);

    /**
     * Lấy danh sách số điện thoại theo contactId.
     * @param contactId ID liên hệ @return danh sách
     */
    List<ContactPhone> findAllByContactId(Long contactId);
}
