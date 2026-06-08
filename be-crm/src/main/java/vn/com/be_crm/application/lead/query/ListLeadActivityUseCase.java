package vn.com.be_crm.application.lead.query;

import vn.com.be_crm.application.lead.dto.LeadActivityResult;
import vn.com.be_crm.application.lead.mapper.LeadActivityCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.lead.repository.ILeadActivityRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy danh sách hoạt động tiềm năng theo leadId. */
public class ListLeadActivityUseCase implements IUseCase<Long, List<LeadActivityResult>> {
    private final ILeadActivityRepository repo;
    /** @param repo port lưu trữ */
    public ListLeadActivityUseCase(ILeadActivityRepository repo) { this.repo = repo; }

    /**
     * Lấy danh sách LeadActivity theo leadId.
     * @param leadId ID tiềm năng @return danh sách result
     */
    @Override
    public List<LeadActivityResult> execute(Long leadId) {
        return repo.findAllByLeadId(leadId).stream()
                .map(LeadActivityCommandMapper::toResult).collect(Collectors.toList());
    }
}
