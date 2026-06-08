package vn.com.be_crm.application.customer.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.customer.repository.IInventoryCheckRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case xóa phiếu kiểm kho. */
public class DeleteInventoryCheckUseCase implements IUseCase<Long, Void> {
    private final IInventoryCheckRepository repo;
    /** @param repo port lưu trữ */
    public DeleteInventoryCheckUseCase(IInventoryCheckRepository repo) { this.repo = repo; }

    /**
     * Xóa InventoryCheck theo ID.
     * @param id ID cần xóa @return null
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("InventoryCheck not found: " + id));
        repo.deleteById(id);
        return null;
    }
}
