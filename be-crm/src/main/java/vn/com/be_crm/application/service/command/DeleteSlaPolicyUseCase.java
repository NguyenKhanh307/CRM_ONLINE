package vn.com.be_crm.application.service.command;

import vn.com.be_crm.core.error.frontend.NotFoundException;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.service.repository.ISlaPolicyRepository;

/** Use case xóa chính sách SLA. */
public class DeleteSlaPolicyUseCase implements IUseCase<Long, Void> {
    private final ISlaPolicyRepository repo;
    /** @param repo port lưu trữ */
    public DeleteSlaPolicyUseCase(ISlaPolicyRepository repo) { this.repo = repo; }
    /** @param id ID @return null @throws NotFoundException */
    @Override public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("SlaPolicy", id));
        repo.deleteById(id); return null;
    }
}
