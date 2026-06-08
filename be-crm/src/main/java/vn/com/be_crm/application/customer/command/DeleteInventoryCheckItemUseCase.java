package vn.com.be_crm.application.customer.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.customer.repository.IInventoryCheckItemRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case xóa dòng kiểm kho. */
public class DeleteInventoryCheckItemUseCase implements IUseCase<Long, Void> {
    private final IInventoryCheckItemRepository repo;
    /** @param repo port lưu trữ */
    public DeleteInventoryCheckItemUseCase(IInventoryCheckItemRepository repo) { this.repo = repo; }

    /**
     * Xóa InventoryCheckItem theo ID.
     * @param id ID cần xóa @return null
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("InventoryCheckItem not found: " + id));
        repo.deleteById(id);
        return null;
    }
}
