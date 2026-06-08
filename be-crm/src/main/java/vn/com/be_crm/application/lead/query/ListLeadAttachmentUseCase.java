package vn.com.be_crm.application.lead.query;

import vn.com.be_crm.application.lead.dto.LeadAttachmentResult;
import vn.com.be_crm.application.lead.mapper.LeadAttachmentCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.lead.repository.ILeadAttachmentRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy danh sách tệp đính kèm tiềm năng theo leadId. */
public class ListLeadAttachmentUseCase implements IUseCase<Long, List<LeadAttachmentResult>> {
    private final ILeadAttachmentRepository repo;
    /** @param repo port lưu trữ */
    public ListLeadAttachmentUseCase(ILeadAttachmentRepository repo) { this.repo = repo; }

    /**
     * Lấy danh sách LeadAttachment theo leadId.
     * @param leadId ID tiềm năng @return danh sách result
     */
    @Override
    public List<LeadAttachmentResult> execute(Long leadId) {
        return repo.findAllByLeadId(leadId).stream()
                .map(LeadAttachmentCommandMapper::toResult).collect(Collectors.toList());
    }
}
