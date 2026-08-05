package vn.com.be_crm.application.lead.query;

import vn.com.be_crm.application.lead.dto.LeadItemResult;
import vn.com.be_crm.application.lead.mapper.LeadItemMapper;
import vn.com.be_crm.domain.lead.repository.ILeadItemRepository;

import java.util.List;

// liệt kê sản phẩm quan tâm/đã yêu cầu báo giá của một tiềm năng
public class ListLeadItemUseCase {
    private final ILeadItemRepository repo;

    public ListLeadItemUseCase(ILeadItemRepository repo) { this.repo = repo; }

    public List<LeadItemResult> execute(Long leadId) {
        return repo.findAllByLeadId(leadId).stream().map(LeadItemMapper::toResult).toList();
    }
}
