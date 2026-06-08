package vn.com.be_crm.application.warehouse.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.application.warehouse.dto.AssignWarehousePermissionCommand;
import vn.com.be_crm.domain.warehouse.repository.IWarehousePermissionRepository;

/** Use case thu hồi quyền kho khỏi người dùng. */
public class RevokeWarehousePermissionUseCase implements IUseCase<AssignWarehousePermissionCommand, Void> {
    private final IWarehousePermissionRepository repo;
    /** @param repo port lưu trữ */
    public RevokeWarehousePermissionUseCase(IWarehousePermissionRepository repo) { this.repo = repo; }
    /** @param c command @return null */
    @Override public Void execute(AssignWarehousePermissionCommand c) {
        repo.deleteByWarehouseIdAndUserId(c.getWarehouseId(), c.getUserId()); return null;
    }
}
