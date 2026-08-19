package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.customer.dto.CustomerResult;
import vn.com.be_crm.application.customer.mapper.CustomerCommandMapper;
import vn.com.be_crm.application.lead.dto.LeadResult;
import vn.com.be_crm.application.lead.mapper.LeadCommandMapper;
import vn.com.be_crm.application.notification.command.CreateNotificationUseCase;
import vn.com.be_crm.core.notify.port.IManagerResolver;
import vn.com.be_crm.domain.customer.entity.Customer;
import vn.com.be_crm.domain.customer.enums.CustomerStatus;
import vn.com.be_crm.domain.customer.enums.CustomerType;
import vn.com.be_crm.domain.customer.repository.ICustomerRepository;
import vn.com.be_crm.domain.lead.entity.Lead;
import vn.com.be_crm.domain.lead.enums.LeadStatus;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;
import vn.com.be_crm.domain.opportunity.entity.Opportunity;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository;
import vn.com.be_crm.core.error.frontend.DomainException;
import vn.com.be_crm.core.error.frontend.NotFoundException;
import vn.com.be_crm.core.tx.port.ITransactionRunner;

// điều phối hành động trạng thái tiềm năng (không sửa tay): claim, convertToCustomer.
// chỉ tạo Khách hàng (không tạo Liên hệ), re-link Cơ hội
// đã gắn sẵn (lead.convertedOpportunityId) sang Khách hàng vừa tạo, rồi xóa mềm Lead.
// Đã bỏ hẳn qualify/lose — vòng đời chỉ còn new/contacting/converted.
public class LeadWorkflowUseCase {
    private final ILeadRepository repo;
    private final ICustomerRepository customerRepo;
    private final IOpportunityRepository opportunityRepo;
    private final CreateNotificationUseCase createNotificationUC;
    private final IManagerResolver managerResolver;
    private final ITransactionRunner tx;

    public LeadWorkflowUseCase(ILeadRepository repo, ICustomerRepository customerRepo,
            IOpportunityRepository opportunityRepo, CreateNotificationUseCase createNotificationUC,
            IManagerResolver managerResolver, ITransactionRunner tx) {
        this.repo = repo;
        this.customerRepo = customerRepo;
        this.opportunityRepo = opportunityRepo;
        this.createNotificationUC = createNotificationUC;
        this.managerResolver = managerResolver;
        this.tx = tx;
    }

    // nhân viên tự nhận chăm sóc tiềm năng đang chưa có người phụ trách (pool
    // chung) — chạy
    // trong transaction để tránh race hai nhân viên bấm cùng lúc, kiểm tra lại
    // ownerId==null
    // bên trong transaction trước khi ghi. Báo cho quản lý trực tiếp biết ai vừa
    // nhận việc —
    // đây chính là kiểu "tin do nhân viên chủ động chuyển qua" mà quản lý cần nhận
    // (khác lead_hot
    // không còn cc quản lý nữa)
    public LeadResult claim(Long id, Long userId) {
        return tx.call(() -> {
            Lead lead = load(id);
            if (lead.getOwnerId() != null) {
                throw new DomainException("Tiềm năng đã có người phụ trách");
            }
            if (lead.getStatus() == LeadStatus.converted) {
                throw new DomainException("Không thể nhận chăm sóc tiềm năng đã chuyển đổi");
            }
            Lead saved = repo.save(lead.toBuilder().ownerId(userId).build());
            notifyManagerClaimed(saved);
            return LeadCommandMapper.toResult(saved);
        });
    }

    // báo quản lý trực tiếp của người vừa nhận việc
    private void notifyManagerClaimed(Lead lead) {
        java.util.List<Long> managers = managerResolver.managersOf(lead.getOwnerId());
        if (managers.isEmpty())
            return;
        String title = "Nhận phụ trách tiềm năng: " + lead.getCode();
        String content = "Nhân viên vừa nhận phụ trách tiềm năng " + lead.getName() + " (" + lead.getCode() + ").";
        createNotificationUC.execute(managers, "lead_claimed", title, content, "lead", lead.getId());
    }

    // chuyển tiềm năng thành khách hàng chính thức: chỉ cho phép khi đã có đơn hàng
    // đầu tiên
    // (chuỗi convertedOpportunityId -> Cơ hội -> Báo giá -> Đơn hàng) — tại đây
    // lead chắc chắn
    // đã có convertedOpportunityId. Tạo Khách hàng từ thông tin Lead (không tạo
    // Liên hệ), re-link
    // Cơ hội đang gắn sang Khách hàng vừa tạo, rồi xóa mềm Lead — chạy trong MỘT
    // transaction
    public CustomerResult convertToCustomer(Long leadId, Long userId) {
        return tx.call(() -> {
            Lead lead = load(leadId);
            if (!repo.hasAnyOrder(leadId, null)) {
                throw new DomainException(
                        "Chỉ có thể chuyển đổi tiềm năng thành khách hàng khi đã có đơn hàng đầu tiên");
            }

            String name = lead.getCompanyName() != null && !lead.getCompanyName().isBlank()
                    ? lead.getCompanyName()
                    : lead.getName();
            Customer customer = customerRepo.save(Customer.builder()
                    .code("KH-" + System.currentTimeMillis())
                    .name(name).type(CustomerType.company)
                    .taxCode(lead.getTaxCode()).phone(lead.getPhone()).email(lead.getEmail())
                    .website(lead.getWebsite()).industry(lead.getIndustry()).source(lead.getSource())
                    .status(CustomerStatus.active)
                    .ownerId(lead.getOwnerId() != null ? lead.getOwnerId() : userId)
                    .build());

            Opportunity opportunity = opportunityRepo.findById(lead.getConvertedOpportunityId())
                    .orElseThrow(
                            () -> new NotFoundException("Opportunity not found: " + lead.getConvertedOpportunityId()));
            opportunityRepo.save(opportunity.toBuilder().customerId(customer.getId()).build());

            repo.deleteById(leadId, userId);
            return CustomerCommandMapper.toResult(customer);
        });
    }

    private Lead load(Long id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Lead not found: " + id));
    }
}
