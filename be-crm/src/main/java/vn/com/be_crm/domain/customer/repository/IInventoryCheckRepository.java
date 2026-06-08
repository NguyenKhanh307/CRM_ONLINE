package vn.com.be_crm.domain.customer.repository;

import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.customer.entity.InventoryCheck;

import java.util.Optional;

/**
 * Port lưu trữ cho InventoryCheck.
 */
public interface IInventoryCheckRepository {

    /**
     * Lưu mới hoặc cập nhật phiếu kiểm kho.
     * @param check domain entity @return entity sau khi lưu
     */
    InventoryCheck save(InventoryCheck check);

    /**
     * Tìm phiếu kiểm kho theo ID.
     * @param id ID @return Optional
     */
    Optional<InventoryCheck> findById(Long id);

    /**
     * Xóa phiếu kiểm kho theo ID.
     * @param id ID cần xóa
     */
    void deleteById(Long id);

    /**
     * Lấy danh sách phiếu kiểm kho có phân trang.
     * @param r tham số phân trang @return PageResult
     */
    PageResult<InventoryCheck> findAll(PageRequest r);
}
