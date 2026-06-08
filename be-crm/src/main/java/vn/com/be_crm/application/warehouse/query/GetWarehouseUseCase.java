package vn.com.be_crm.application.warehouse.query;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.application.warehouse.dto.WarehouseResult;
import vn.com.be_crm.application.warehouse.mapper.WarehouseCommandMapper;
import vn.com.be_crm.domain.shared.exception.NotFoundException;
import vn.com.be_crm.domain.warehouse.repository.IWarehouseRepository;

/** Use case lấy kho hàng theo ID. */
public class GetWarehouseUseCase implements IUseCase<Long, WarehouseResult> {
    private final IWarehouseRepository repo;
    /** @param repo port lưu trữ */
    public GetWarehouseUseCase(IWarehouseRepository repo) { this.repo = repo; }
    /** @param id ID @return result @throws NotFoundException */
    @Override public WarehouseResult execute(Long id) {
        return repo.findById(id).map(WarehouseCommandMapper::toResult)
                .orElseThrow(() -> new NotFoundException("Warehouse", id));
    }
}
