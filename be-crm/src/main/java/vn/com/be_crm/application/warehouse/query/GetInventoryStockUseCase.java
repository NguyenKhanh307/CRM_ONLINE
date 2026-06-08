package vn.com.be_crm.application.warehouse.query;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.application.warehouse.dto.InventoryStockResult;
import vn.com.be_crm.application.warehouse.mapper.WarehouseCommandMapper;
import vn.com.be_crm.domain.shared.exception.NotFoundException;
import vn.com.be_crm.domain.warehouse.repository.IInventoryStockRepository;

/** Use case lấy tồn kho theo ID. */
public class GetInventoryStockUseCase implements IUseCase<Long, InventoryStockResult> {
    private final IInventoryStockRepository repo;
    /** @param repo port lưu trữ */
    public GetInventoryStockUseCase(IInventoryStockRepository repo) { this.repo = repo; }
    /** @param id ID @return result @throws NotFoundException */
    @Override public InventoryStockResult execute(Long id) {
        return repo.findById(id).map(WarehouseCommandMapper::toStockResult)
                .orElseThrow(() -> new NotFoundException("InventoryStock", id));
    }
}
