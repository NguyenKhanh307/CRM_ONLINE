package vn.com.be_crm.application.warehouse.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.application.warehouse.dto.*;
import vn.com.be_crm.application.warehouse.mapper.WarehouseCommandMapper;
import vn.com.be_crm.domain.warehouse.repository.IWarehouseRepository;

/** Use case tạo mới kho hàng. */
public class CreateWarehouseUseCase implements IUseCase<CreateWarehouseCommand, WarehouseResult> {
    private final IWarehouseRepository repo;
    /** @param repo port lưu trữ */
    public CreateWarehouseUseCase(IWarehouseRepository repo) { this.repo = repo; }
    /** @param c command @return result */
    @Override public WarehouseResult execute(CreateWarehouseCommand c) {
        return WarehouseCommandMapper.toResult(repo.save(WarehouseCommandMapper.toEntity(c)));
    }
}
