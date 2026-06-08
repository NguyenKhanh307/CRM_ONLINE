package vn.com.be_crm.domain.contact.repository;

import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.contact.entity.Contact;

import java.util.Optional;

/**
 * Port lưu trữ cho Contact.
 */
public interface IContactRepository {

    /**
     * Lưu mới hoặc cập nhật liên hệ.
     * @param contact domain entity @return entity sau khi lưu
     */
    Contact save(Contact contact);

    /**
     * Tìm liên hệ theo ID (chưa xóa mềm).
     * @param id ID @return Optional
     */
    Optional<Contact> findById(Long id);

    /**
     * Xóa mềm liên hệ theo ID.
     * @param id ID cần xóa
     */
    void deleteById(Long id);

    /**
     * Lấy danh sách liên hệ chưa xóa có phân trang.
     * @param r tham số phân trang @return PageResult
     */
    PageResult<Contact> findAll(PageRequest r);
}
