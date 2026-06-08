package vn.com.be_crm.application.lead.query;

import vn.com.be_crm.application.lead.dto.LeadResult;
import vn.com.be_crm.application.lead.mapper.LeadCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case lấy tiềm năng theo ID. */
public class GetLeadUseCase implements IUseCase<Long, LeadResult> {
    private final ILeadRepository repo;
    /** @param repo port lưu trữ */
    public GetLeadUseCase(ILeadRepository repo) { this.repo = repo; }

    /**
     * Lấy Lead theo ID.
     * @param id ID @return LeadResult
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public LeadResult execute(Long id) {
        return LeadCommandMapper.toResult(
                repo.findById(id).orElseThrow(() -> new NotFoundException("Lead not found: " + id)));
    }
}
