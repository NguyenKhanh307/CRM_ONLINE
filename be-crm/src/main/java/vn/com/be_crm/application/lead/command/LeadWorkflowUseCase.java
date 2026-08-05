package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.lead.dto.LeadResult;
import vn.com.be_crm.application.lead.mapper.LeadCommandMapper;
import vn.com.be_crm.domain.lead.entity.Lead;
import vn.com.be_crm.domain.lead.enums.LeadStatus;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;
import vn.com.be_crm.core.error.frontend.DomainException;
import vn.com.be_crm.core.error.frontend.NotFoundException;
import vn.com.be_crm.core.tx.port.ITransactionRunner;

// điều phối hành động trạng thái tiềm năng (không sửa tay): claim.
// Không còn "convert" tự động tách Khách hàng + Liên hệ + Cơ hội — mỗi bản ghi giờ được
// tạo riêng bằng tay; Lead.contactId/convertedOpportunityId (nếu người dùng muốn gán) sửa
// qua UpdateLeadUseCase như field thường, trạng thái "converted" cũng tự đổi tay qua Update
// (có guard riêng: chỉ cho phép khi tiềm năng đã có đơn hàng đầu tiên).
// Đã bỏ hẳn qualify/lose — vòng đời chỉ còn new/contacting/converted.
public class LeadWorkflowUseCase {
    private final ILeadRepository repo;
    private final ITransactionRunner tx;

    public LeadWorkflowUseCase(ILeadRepository repo, ITransactionRunner tx) {
        this.repo = repo;
        this.tx = tx;
    }

    // nhân viên tự nhận chăm sóc tiềm năng đang chưa có người phụ trách (pool chung) — chạy
    // trong transaction để tránh race hai nhân viên bấm cùng lúc, kiểm tra lại ownerId==null
    // bên trong transaction trước khi ghi
    public LeadResult claim(Long id, Long userId) {
        return tx.call(() -> {
            Lead lead = load(id);
            if (lead.getOwnerId() != null) {
                throw new DomainException("Tiềm năng đã có người phụ trách");
            }
            if (lead.getStatus() == LeadStatus.converted) {
                throw new DomainException("Không thể nhận chăm sóc tiềm năng đã chuyển đổi");
            }
            return LeadCommandMapper.toResult(repo.save(lead.toBuilder().ownerId(userId).build()));
        });
    }

    private Lead load(Long id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Lead not found: " + id));
    }
}
