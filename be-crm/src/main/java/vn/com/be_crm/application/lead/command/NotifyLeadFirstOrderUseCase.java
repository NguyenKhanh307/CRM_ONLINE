package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.notification.command.CreateNotificationUseCase;
import vn.com.be_crm.domain.auth.repository.IUserRoleRepository;
import vn.com.be_crm.domain.lead.entity.Lead;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;

import java.util.List;

// gọi sau khi tạo Đơn hàng — nếu đơn vừa tạo là đơn hàng ĐẦU TIÊN thuộc chuỗi cơ hội đã chuyển
// đổi của một tiềm năng, báo để cân nhắc chuyển tiềm năng đó thành khách hàng chính thức
// (status=converted vẫn là thao tác tay, xem UpdateLeadUseCase). Chưa có người phụ trách -> báo
// toàn bộ nhân viên; đã có -> chỉ báo đúng người đó (quản lý không cc, cùng quy tắc AddLeadScoreUseCase)
public class NotifyLeadFirstOrderUseCase {
    private static final List<String> STAFF_ROLES = List.of("SALES_STAFF");

    private final ILeadRepository leadRepo;
    private final CreateNotificationUseCase createNotificationUC;
    private final IUserRoleRepository userRoleRepo;

    public NotifyLeadFirstOrderUseCase(ILeadRepository leadRepo, CreateNotificationUseCase createNotificationUC,
                                        IUserRoleRepository userRoleRepo) {
        this.leadRepo = leadRepo;
        this.createNotificationUC = createNotificationUC;
        this.userRoleRepo = userRoleRepo;
    }

    // quotationId: báo giá của đơn vừa tạo (có thể null nếu đơn tạo tay không gắn báo giá);
    // newOrderId: id đơn vừa lưu, dùng để loại trừ khi đếm "đã có đơn nào khác chưa"
    public void execute(Long quotationId, Long newOrderId) {
        if (quotationId == null) return;
        Lead lead = leadRepo.findByQuotationId(quotationId).orElse(null);
        if (lead == null) return;
        // đã có đơn khác trước đó -> không phải đơn đầu tiên, không báo lại
        if (leadRepo.hasAnyOrder(lead.getId(), newOrderId)) return;

        List<Long> recipients = lead.getOwnerId() != null
                ? List.of(lead.getOwnerId())
                : userRoleRepo.findUserIdsByRoleCodes(STAFF_ROLES);
        String title = "Đơn hàng đầu tiên: " + lead.getCode();
        String content = "Tiềm năng " + lead.getName() + " (" + lead.getCode() + ") đã phát sinh đơn hàng đầu tiên"
                + " — cân nhắc chuyển đổi thành khách hàng chính thức.";
        createNotificationUC.execute(recipients, "lead_first_order", title, content, "lead", lead.getId());
    }
}
