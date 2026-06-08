package vn.com.be_crm.domain.warehouse.repository;

import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.warehouse.entity.InventoryStock;

import java.util.Optional;

/** Port lưu trữ cho InventoryStock. Không có deleteById — xóa qua cascade. */
public interface IInventoryStockRepository {
    InventoryStock save(InventoryStock s);
    Optional<InventoryStock> findById(Long id);
    PageResult<InventoryStock> findAll(PageRequest r);
}
