package vn.com.be_crm.application.warehouse.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.application.warehouse.dto.AssignWarehousePermissionCommand;
import vn.com.be_crm.domain.warehouse.entity.WarehousePermission;
import vn.com.be_crm.domain.warehouse.enums.WarehousePermissionType;
import vn.com.be_crm.domain.warehouse.repository.IWarehousePermissionRepository;

/** Use case gán quyền kho cho người dùng. */
public class AssignWarehousePermissionUseCase implements IUseCase<AssignWarehousePermissionCommand, Void> {
    private final IWarehousePermissionRepository repo;
    /** @param repo port lưu trữ */
    public AssignWarehousePermissionUseCase(IWarehousePermissionRepository repo) { this.repo = repo; }
    /** @param c command @return null */
    @Override public Void execute(AssignWarehousePermissionCommand c) {
        repo.save(WarehousePermission.builder().warehouseId(c.getWarehouseId()).userId(c.getUserId())
                .permission(c.getPermission() != null ? c.getPermission() : WarehousePermissionType.view).build());
        return null;
    }
}
