package vn.com.be_crm.application.lead.query;

import vn.com.be_crm.application.lead.dto.LeadTransferResult;
import vn.com.be_crm.application.lead.mapper.LeadTransferCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.lead.repository.ILeadTransferRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy danh sách chuyển giao tiềm năng theo leadId. */
public class ListLeadTransferUseCase implements IUseCase<Long, List<LeadTransferResult>> {
    private final ILeadTransferRepository repo;
    /** @param repo port lưu trữ */
    public ListLeadTransferUseCase(ILeadTransferRepository repo) { this.repo = repo; }

    /**
     * Lấy danh sách LeadTransfer theo leadId.
     * @param leadId ID tiềm năng @return danh sách result
     */
    @Override
    public List<LeadTransferResult> execute(Long leadId) {
        return repo.findAllByLeadId(leadId).stream()
                .map(LeadTransferCommandMapper::toResult).collect(Collectors.toList());
    }
}
