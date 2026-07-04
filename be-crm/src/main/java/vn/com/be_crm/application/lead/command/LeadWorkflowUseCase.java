package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.lead.dto.LeadResult;
import vn.com.be_crm.application.lead.mapper.LeadCommandMapper;
import vn.com.be_crm.domain.contact.entity.Contact;
import vn.com.be_crm.domain.contact.repository.IContactRepository;
import vn.com.be_crm.domain.customer.entity.Customer;
import vn.com.be_crm.domain.customer.enums.CustomerStatus;
import vn.com.be_crm.domain.customer.enums.CustomerType;
import vn.com.be_crm.domain.customer.repository.ICustomerRepository;
import vn.com.be_crm.domain.lead.entity.Lead;
import vn.com.be_crm.domain.lead.enums.LeadStatus;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;
import vn.com.be_crm.domain.opportunity.entity.Opportunity;
import vn.com.be_crm.domain.opportunity.enums.OpportunityStatus;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

import java.math.BigDecimal;

/**
 * Use case điều phối hành động trạng thái tiềm năng (không sửa tay):
 * convert (qualified → converted, tách thành Khách hàng + Liên hệ + Cơ hội) và lose (→ lost).
 */
public class LeadWorkflowUseCase {
    private final ILeadRepository repo;
    private final ICustomerRepository customerRepo;
    private final IContactRepository contactRepo;
    private final IOpportunityRepository opportunityRepo;

    /** @param repo Lead @param customerRepo Khách hàng @param contactRepo Liên hệ @param opportunityRepo Cơ hội */
    public LeadWorkflowUseCase(ILeadRepository repo, ICustomerRepository customerRepo,
                               IContactRepository contactRepo, IOpportunityRepository opportunityRepo) {
        this.repo = repo;
        this.customerRepo = customerRepo;
        this.contactRepo = contactRepo;
        this.opportunityRepo = opportunityRepo;
    }

    /**
     * Chuyển đổi tiềm năng thành công (qualified → converted): tách dữ liệu phẳng thành
     * Khách hàng (Account) + Liên hệ (Contact) + Cơ hội (Opportunity) theo mô hình B2B, rồi khóa tiềm năng.
     * @param id ID tiềm năng @return tiềm năng sau cập nhật
     */
    public LeadResult convert(Long id) {
        Lead lead = load(id);
        lead.getStatus().ensureCanTransitionTo(LeadStatus.converted);

        long now = System.currentTimeMillis();

        // 1) Tạo Khách hàng (mỏ neo B2B) từ thông tin tổ chức của tiềm năng
        Customer customer = customerRepo.save(Customer.builder()
                .code("KH-" + now)
                .name(lead.getCompanyName() != null && !lead.getCompanyName().isBlank() ? lead.getCompanyName() : lead.getName())
                .type(CustomerType.company)
                .status(CustomerStatus.active)
                .taxCode(lead.getTaxCode()).phone(lead.getPhone()).email(lead.getEmail())
                .website(lead.getWebsite()).industry(lead.getIndustry()).source(lead.getSource())
                .ownerId(lead.getOwnerId())
                .build());

        // 2) Tạo Liên hệ (cá nhân ra quyết định) gắn vào Khách hàng vừa tạo
        Contact contact = contactRepo.save(Contact.builder()
                .customerId(customer.getId())
                .assignedUserId(lead.getOwnerId())
                .fullName(lead.getName()).title(lead.getTitle()).department(lead.getDepartment())
                .email(lead.getEmail()).source(lead.getSource())
                .doNotCall(lead.isDoNotCall()).doNotEmail(lead.isDoNotEmail())
                .isPrimary(true)
                .build());

        // 3) Tạo Cơ hội (giao dịch kỳ vọng đầu tiên) liên kết Khách hàng + Liên hệ
        BigDecimal estimated = lead.getEstimatedValue() != null ? lead.getEstimatedValue() : BigDecimal.ZERO;
        Opportunity opportunity = opportunityRepo.save(Opportunity.builder()
                .code("CH-" + now)
                .name("Cơ hội từ " + (lead.getCompanyName() != null && !lead.getCompanyName().isBlank() ? lead.getCompanyName() : lead.getName()))
                .opportunityType("KH mới")
                .customerId(customer.getId()).contactId(contact.getId()).ownerId(lead.getOwnerId())
                .amount(estimated).expectedRevenue(estimated)
                .source(lead.getSource()).campaignId(lead.getCampaignId())
                .status(OpportunityStatus.open)
                .build());

        // 4) Khóa tiềm năng: chuyển converted + lưu liên kết tới 3 bản ghi vừa tạo (không dùng tiềm năng nữa)
        Lead saved = repo.save(lead.toBuilder()
                .status(LeadStatus.converted)
                .customerId(customer.getId())
                .contactId(contact.getId())
                .convertedOpportunityId(opportunity.getId())
                .build());
        return LeadCommandMapper.toResult(saved);
    }

    /**
     * Đánh dấu tiềm năng thất bại (→ lost), ghi lý do vào ghi chú.
     * @param id ID tiềm năng @param reason lý do thất bại (có thể null)
     * @return tiềm năng sau cập nhật
     */
    public LeadResult lose(Long id, String reason) {
        Lead lead = load(id);
        lead.getStatus().ensureCanTransitionTo(LeadStatus.lost);
        String note = reason == null || reason.isBlank() ? lead.getNote()
                : (lead.getNote() == null || lead.getNote().isBlank() ? "" : lead.getNote() + " | ") + "Lý do thất bại: " + reason;
        return LeadCommandMapper.toResult(repo.save(lead.toBuilder().status(LeadStatus.lost).note(note).build()));
    }

    /** Tải tiềm năng theo ID hoặc ném NotFoundException. */
    private Lead load(Long id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Lead not found: " + id));
    }
}
