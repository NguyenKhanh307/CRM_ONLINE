package vn.com.be_crm.domain.lead.repository;

import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.lead.entity.Lead;

import java.util.Optional;

/**
 * Port lưu trữ cho Lead.
 */
public interface ILeadRepository {

    /**
     * Lưu mới hoặc cập nhật tiềm năng.
     * @param lead domain entity @return entity sau khi lưu
     */
    Lead save(Lead lead);

    /**
     * Tìm tiềm năng theo ID (chưa xóa mềm).
     * @param id ID @return Optional
     */
    Optional<Lead> findById(Long id);

    /**
     * Xóa mềm tiềm năng theo ID.
     * @param id ID cần xóa
     */
    void deleteById(Long id);

    /**
     * Lấy danh sách tiềm năng chưa xóa có phân trang.
     * @param r tham số phân trang @return PageResult
     */
    PageResult<Lead> findAll(PageRequest r);
}
