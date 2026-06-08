package vn.com.be_crm.application.warehouse.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.shared.exception.NotFoundException;
import vn.com.be_crm.domain.warehouse.repository.IWarehouseRepository;

/** Use case xóa kho hàng. */
public class DeleteWarehouseUseCase implements IUseCase<Long, Void> {
    private final IWarehouseRepository repo;
    /** @param repo port lưu trữ */
    public DeleteWarehouseUseCase(IWarehouseRepository repo) { this.repo = repo; }
    /** @param id ID @return null @throws NotFoundException */
    @Override public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("Warehouse", id));
        repo.deleteById(id); return null;
    }
}
