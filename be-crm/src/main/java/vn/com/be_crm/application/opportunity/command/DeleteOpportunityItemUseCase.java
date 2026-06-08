package vn.com.be_crm.application.opportunity.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityItemRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case xóa dòng sản phẩm trong cơ hội. */
public class DeleteOpportunityItemUseCase implements IUseCase<Long, Void> {
    private final IOpportunityItemRepository repo;
    /** @param repo port lưu trữ */
    public DeleteOpportunityItemUseCase(IOpportunityItemRepository repo) { this.repo = repo; }

    /**
     * Xóa OpportunityItem theo ID.
     * @param id ID cần xóa @return null
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("OpportunityItem not found: " + id));
        repo.deleteById(id); return null;
    }
}
