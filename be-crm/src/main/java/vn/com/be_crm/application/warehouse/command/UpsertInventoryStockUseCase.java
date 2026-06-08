package vn.com.be_crm.application.warehouse.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.application.warehouse.dto.*;
import vn.com.be_crm.application.warehouse.mapper.WarehouseCommandMapper;
import vn.com.be_crm.domain.warehouse.repository.IInventoryStockRepository;

/** Use case tạo mới hoặc cập nhật tồn kho. */
public class UpsertInventoryStockUseCase implements IUseCase<UpsertInventoryStockCommand, InventoryStockResult> {
    private final IInventoryStockRepository repo;
    /** @param repo port lưu trữ */
    public UpsertInventoryStockUseCase(IInventoryStockRepository repo) { this.repo = repo; }
    /** @param c command @return result */
    @Override public InventoryStockResult execute(UpsertInventoryStockCommand c) {
        return WarehouseCommandMapper.toStockResult(repo.save(WarehouseCommandMapper.toEntity(c)));
    }
}
