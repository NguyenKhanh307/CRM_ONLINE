package vn.com.be_crm.domain.lead.repository;

import vn.com.be_crm.core.dto.delete.DeletedItemResult;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.page.PageResult;
import vn.com.be_crm.domain.lead.entity.Lead;

import java.util.List;
import java.util.Optional;

// port lưu trữ cho Lead
public interface ILeadRepository {

    Lead save(Lead lead);

    Optional<Lead> findById(Long id);

    void deleteById(Long id, Long deletedBy);

    PageResult<Lead> findAll(PageRequest r);

    // thùng rác — chỉ bản ghi xóa mềm trong 30 ngày gần nhất, isAdmin=false thì chỉ của chính mình
    PageResult<DeletedItemResult> findDeleted(Long userId, boolean isAdmin, PageRequest r);

    void restoreById(Long id);

    // set is_purged=true — ẩn khỏi thùng rác, DB vẫn giữ bản ghi soft-delete
    void purgeById(Long id);

    // dùng để match dòng UPDATE khi import file
    Optional<Lead> findByPhone(String phone);

    Optional<Lead> findByEmail(String email);

    // dùng cho web tracking (mã TNW...)
    Optional<Lead> findByCode(String code);

    // isAdminOrManager=false thì chỉ bàn giao được bản ghi mình đang là owner
    void handoverBulk(List<Long> ids, Long toUserId, Long currentUserId, boolean isAdminOrManager);

    void handoverAll(Long fromUserId, Long toUserId);
}
