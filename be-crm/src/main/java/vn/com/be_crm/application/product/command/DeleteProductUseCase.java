package vn.com.be_crm.application.product.command;

import vn.com.be_crm.core.dto.delete.DeleteCommand;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.product.repository.IProductRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/** Use case xóa mềm hàng hóa. */
public class DeleteProductUseCase implements IUseCase<DeleteCommand, Void> {
    private final IProductRepository repo;
    /** @param repo port lưu trữ */
    public DeleteProductUseCase(IProductRepository repo) { this.repo = repo; }
    /**
     * Xóa mềm Product theo ID, ghi nhận người xóa.
     * @param cmd command chứa id và deletedBy @return null @throws NotFoundException
     */
    @Override
    public Void execute(DeleteCommand cmd) {
        repo.findById(cmd.id()).orElseThrow(() -> new NotFoundException("Product", cmd.id()));
        repo.deleteById(cmd.id(), cmd.deletedBy());
        return null;
    }
}
