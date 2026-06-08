package vn.com.be_crm.application.warehouse.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.application.warehouse.dto.*;
import vn.com.be_crm.application.warehouse.mapper.WarehouseCommandMapper;
import vn.com.be_crm.domain.warehouse.repository.IWarehouseRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case cập nhật kho hàng. */
public class UpdateWarehouseUseCase implements IUseCase<UpdateWarehouseCommand, WarehouseResult> {
    private final IWarehouseRepository repo;
    /** @param repo port lưu trữ */
    public UpdateWarehouseUseCase(IWarehouseRepository repo) { this.repo = repo; }
    /** @param c command @return result @throws NotFoundException */
    @Override public WarehouseResult execute(UpdateWarehouseCommand c) {
        var e = repo.findById(c.getId()).orElseThrow(() -> new NotFoundException("Warehouse", c.getId()));
        return WarehouseCommandMapper.toResult(repo.save(WarehouseCommandMapper.toEntity(c, e)));
    }
}
