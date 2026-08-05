package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.lead.dto.LeadResult;
import vn.com.be_crm.application.lead.dto.UpdateLeadCommand;
import vn.com.be_crm.application.lead.mapper.LeadCommandMapper;
import vn.com.be_crm.core.notify.NotifyAssignmentUseCase;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.lead.entity.Lead;
import vn.com.be_crm.domain.lead.enums.LeadStatus;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;
import vn.com.be_crm.core.error.frontend.DomainException;
import vn.com.be_crm.core.error.frontend.NotFoundException;

import java.util.Objects;

// cập nhật tiềm năng
public class UpdateLeadUseCase implements IUseCase<UpdateLeadCommand, LeadResult> {
    private final ILeadRepository repo;
    private final NotifyAssignmentUseCase notifyUC;

    public UpdateLeadUseCase(ILeadRepository repo, NotifyAssignmentUseCase notifyUC) {
        this.repo = repo;
        this.notifyUC = notifyUC;
    }

    @Override
    public LeadResult execute(UpdateLeadCommand cmd) {
        Lead existing = repo.findById(cmd.getId())
                .orElseThrow(() -> new NotFoundException("Lead not found: " + cmd.getId()));
        // đã chuyển đổi (converted) thì khóa — dữ liệu đã tách sang KH/Liên hệ/Cơ hội, không dùng nữa
        if (existing.getStatus() == LeadStatus.converted) {
            throw new DomainException("Tiềm năng đã chuyển đổi, không thể chỉnh sửa");
        }
        // chuyển sang converted chỉ được phép khi chuỗi Tiềm năng->Cơ hội->Báo giá->Đơn hàng đã có
        // đơn hàng đầu tiên — việc đổi status thật sự vẫn là thao tác tay ở đây, không tự động
        if (cmd.getStatus() == LeadStatus.converted && !repo.hasAnyOrder(cmd.getId(), null)) {
            throw new DomainException("Chỉ có thể chuyển đổi tiềm năng thành khách hàng khi đã có đơn hàng đầu tiên");
        }
        Lead saved = repo.save(LeadCommandMapper.toEntity(cmd, existing));
        // đổi người phụ trách -> báo cho người nhận việc (gồm cả trường hợp quản lý gán tiềm năng vô chủ)
        if (!Objects.equals(existing.getOwnerId(), saved.getOwnerId())) {
            notifyUC.notifyAssigned("lead", "tiềm năng", saved.getOwnerId(), saved.getId(), saved.getCode());
        }
        return LeadCommandMapper.toResult(saved);
    }
}
