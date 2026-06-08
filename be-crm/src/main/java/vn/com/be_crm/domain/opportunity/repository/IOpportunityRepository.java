package vn.com.be_crm.domain.opportunity.repository;

import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.opportunity.entity.Opportunity;

import java.util.Optional;

/**
 * Port lưu trữ cho Opportunity.
 */
public interface IOpportunityRepository {

    /**
     * Lưu mới hoặc cập nhật cơ hội bán hàng.
     * @param opportunity domain entity @return entity sau khi lưu
     */
    Opportunity save(Opportunity opportunity);

    /**
     * Tìm cơ hội theo ID (chưa xóa mềm).
     * @param id ID @return Optional
     */
    Optional<Opportunity> findById(Long id);

    /**
     * Xóa mềm cơ hội theo ID.
     * @param id ID cần xóa
     */
    void deleteById(Long id);

    /**
     * Lấy danh sách cơ hội chưa xóa có phân trang.
     * @param r tham số phân trang @return PageResult
     */
    PageResult<Opportunity> findAll(PageRequest r);
}
