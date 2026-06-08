package vn.com.be_crm.domain.customer.repository;

import vn.com.be_crm.domain.customer.entity.InventoryCheckItem;

import java.util.List;
import java.util.Optional;

/**
 * Port lưu trữ cho InventoryCheckItem.
 */
public interface IInventoryCheckItemRepository {

    /**
     * Lưu mới hoặc cập nhật dòng kiểm kho.
     * @param item domain entity @return entity sau khi lưu
     */
    InventoryCheckItem save(InventoryCheckItem item);

    /**
     * Tìm dòng kiểm kho theo ID.
     * @param id ID @return Optional
     */
    Optional<InventoryCheckItem> findById(Long id);

    /**
     * Xóa dòng kiểm kho theo ID.
     * @param id ID cần xóa
     */
    void deleteById(Long id);

    /**
     * Lấy danh sách dòng kiểm kho theo checkId.
     * @param checkId ID phiếu kiểm kho @return danh sách
     */
    List<InventoryCheckItem> findAllByCheckId(Long checkId);
}
