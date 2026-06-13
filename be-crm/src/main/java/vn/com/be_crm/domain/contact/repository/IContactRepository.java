package vn.com.be_crm.domain.contact.repository;

import vn.com.be_crm.application.shared.dto.DeletedItemResult;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.contact.entity.Contact;
import vn.com.be_crm.domain.contact.entity.ContactPhone;

import java.util.List;
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
     * Lưu liên hệ kèm danh sách số điện thoại trong một transaction.
     * @param contact domain entity liên hệ
     * @param phones  danh sách số điện thoại (contactId sẽ được gán sau khi lưu liên hệ)
     * @return liên hệ sau khi lưu
     */
    Contact saveWithPhones(Contact contact, List<ContactPhone> phones);

    /**
     * Tìm liên hệ theo ID (chưa xóa mềm).
     * @param id ID @return Optional
     */
    Optional<Contact> findById(Long id);

    /**
     * Xóa mềm liên hệ theo ID, ghi nhận người xóa.
     * @param id        ID cần xóa
     * @param deletedBy ID người thực hiện xóa
     */
    void deleteById(Long id, Long deletedBy);

    /**
     * Lấy danh sách liên hệ chưa xóa có phân trang.
     * @param r tham số phân trang @return PageResult
     */
    PageResult<Contact> findAll(PageRequest r);

    /**
     * Lấy danh sách liên hệ đã xóa mềm trong 30 ngày gần nhất (dùng cho thùng rác).
     * @param userId  ID người dùng hiện tại
     * @param isAdmin true nếu admin (xem tất cả), false nếu chỉ xem của mình
     * @param r       tham số phân trang
     * @return PageResult<DeletedItemResult>
     */
    PageResult<DeletedItemResult> findDeleted(Long userId, boolean isAdmin, PageRequest r);

    /**
     * Khôi phục liên hệ từ thùng rác.
     * @param id ID cần khôi phục
     */
    void restoreById(Long id);

    /**
     * Ẩn liên hệ khỏi thùng rác (set is_purged = true).
     * @param id ID cần ẩn
     */
    void purgeById(Long id);
}
