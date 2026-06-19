package vn.com.be_crm.domain.warehouse.repository;

import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.warehouse.entity.Warehouse;

import java.util.Optional;

/** Port lưu trữ cho Warehouse. */
public interface IWarehouseRepository {
    Warehouse save(Warehouse w);
    Optional<Warehouse> findById(Long id);

    /**
     * Tìm kho theo mã (code) — dùng cho import UPDATE/BOTH.
     * @param code mã kho @return Optional
     */
    Optional<Warehouse> findByCode(String code);
    void deleteById(Long id);
    PageResult<Warehouse> findAll(PageRequest r);
}
