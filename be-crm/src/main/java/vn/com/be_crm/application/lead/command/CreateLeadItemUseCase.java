package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.lead.dto.CreateLeadItemCommand;
import vn.com.be_crm.application.lead.dto.LeadItemResult;
import vn.com.be_crm.application.lead.mapper.LeadItemMapper;
import vn.com.be_crm.domain.lead.repository.ILeadItemRepository;

// ghi nhận một sản phẩm mà tiềm năng vừa xem/yêu cầu báo giá (landing page gọi khi khách bấm
// vào sản phẩm hoặc bấm "Yêu cầu báo giá")
public class CreateLeadItemUseCase {
    private final ILeadItemRepository repo;

    public CreateLeadItemUseCase(ILeadItemRepository repo) { this.repo = repo; }

    public LeadItemResult execute(CreateLeadItemCommand cmd) {
        return LeadItemMapper.toResult(repo.save(LeadItemMapper.toEntity(cmd)));
    }
}
