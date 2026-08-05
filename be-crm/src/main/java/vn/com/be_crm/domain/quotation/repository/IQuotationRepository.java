package vn.com.be_crm.domain.quotation.repository;

import vn.com.be_crm.core.dto.delete.DeletedItemResult;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.page.PageResult;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.entity.QuotationItem;

import java.util.List;
import java.util.Optional;

// port lưu trữ cho Quotation
public interface IQuotationRepository {

    Quotation save(Quotation q);

    // tìm báo giá theo mã (code) chưa xóa mềm — dùng cho import UPDATE/BOTH và trang khách phản
    // hồi công khai (mã không bí mật, giống cách phiếu hỗ trợ dùng code)
    Optional<Quotation> findByCode(String code);

    // lưu báo giá kèm danh sách dòng hàng trong một transaction (quotationId gán sau khi lưu báo giá)
    Quotation saveWithItems(Quotation q, List<QuotationItem> items);

    Optional<Quotation> findById(Long id);

    // danh sách báo giá theo cơ hội (chưa xóa mềm) — dùng cho quản lý báo giá đồng bộ (primary)
    List<Quotation> findAllByOpportunityId(Long opportunityId);

    void deleteById(Long id, Long deletedBy);

    PageResult<Quotation> findAll(PageRequest r);

    // danh sách báo giá đã xóa mềm trong 30 ngày gần nhất
    PageResult<DeletedItemResult> findDeleted(Long userId, boolean isAdmin, PageRequest r);

    void restoreById(Long id);

    void purgeById(Long id);

    // bàn giao hàng loạt báo giá sang người dùng mới — isAdminOrManager=true: bàn giao bất kỳ,
    // false: chỉ bàn giao bản ghi mình là owner
    void handoverBulk(List<Long> ids, Long toUserId, Long currentUserId, boolean isAdminOrManager);

    void handoverAll(Long fromUserId, Long toUserId);
}
