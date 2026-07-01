package vn.com.be_crm.application.service.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.service.repository.ITicketReturnItemRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case xóa dòng hàng trả/đổi. */
public class DeleteTicketReturnItemUseCase implements IUseCase<Long, Void> {
    private final ITicketReturnItemRepository repo;
    /** @param repo port lưu trữ */
    public DeleteTicketReturnItemUseCase(ITicketReturnItemRepository repo) { this.repo = repo; }
    /** Xóa TicketReturnItem. @param id @return null @throws NotFoundException */
    @Override public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("TicketReturnItem not found: " + id));
        repo.deleteById(id); return null;
    }
}
