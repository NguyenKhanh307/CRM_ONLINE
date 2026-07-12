package vn.com.be_crm.application.campaign.command;

import vn.com.be_crm.application.shared.util.CrossFieldRules;
import vn.com.be_crm.application.campaign.dto.CreateCampaignCommand;
import vn.com.be_crm.application.campaign.dto.CampaignResult;
import vn.com.be_crm.application.campaign.mapper.CampaignCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.shared.exception.DomainException;
import vn.com.be_crm.domain.campaign.repository.ICampaignRepository;

/** Use case tạo mới chiến dịch. */
public class CreateCampaignUseCase implements IUseCase<CreateCampaignCommand, CampaignResult> {
    private final ICampaignRepository repo;
    /** @param repo port lưu trữ */
    public CreateCampaignUseCase(ICampaignRepository repo) { this.repo = repo; }
    /** Tạo mới Campaign. @param cmd @return CampaignResult */
    @Override public CampaignResult execute(CreateCampaignCommand cmd) {
        // Ràng buộc khoảng thời gian: ngày kết thúc không được trước ngày bắt đầu
        CrossFieldRules.requireDateRange(cmd.getStartDate(), cmd.getEndDate(), "Ngày bắt đầu", "Ngày kết thúc");
        // Check trùng thân thiện trước khi insert (DB unique constraint là lớp phòng thủ 2)
        if (cmd.getCode() != null && repo.findByCode(cmd.getCode()).isPresent()) {
            throw new DomainException("Mã chiến dịch \"" + cmd.getCode() + "\" đã tồn tại, vui lòng dùng mã khác");
        }
        return CampaignCommandMapper.toResult(repo.save(CampaignCommandMapper.toEntity(cmd)));
    }
}
