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
import vn.com.be_crm.core.error.frontend.DomainException;
import vn.com.be_crm.core.error.frontend.NotFoundException;
import vn.com.be_crm.core.security.port.ICurrentUser;
import vn.com.be_crm.core.tx.port.ITransactionRunner;

import java.math.BigDecimal;

// điều phối hành động trạng thái tiềm năng (không sửa tay): convert (qualified -> converted,
// tách thành Khách hàng + Liên hệ + Cơ hội) và lose (-> lost)
public class LeadWorkflowUseCase {
    private final ILeadRepository repo;
    private final ICustomerRepository customerRepo;
    private final IContactRepository contactRepo;
    private final IOpportunityRepository opportunityRepo;
    private final ITransactionRunner tx;
    private final ICurrentUser currentUser;

    public LeadWorkflowUseCase(ILeadRepository repo, ICustomerRepository customerRepo,
                               IContactRepository contactRepo, IOpportunityRepository opportunityRepo,
                               ITransactionRunner tx, ICurrentUser currentUser) {
        this.repo = repo;
        this.customerRepo = customerRepo;
        this.contactRepo = contactRepo;
        this.opportunityRepo = opportunityRepo;
        this.tx = tx;
        this.currentUser = currentUser;
    }

    // tách dữ liệu phẳng thành Khách hàng + Liên hệ + Cơ hội rồi xóa mềm tiềm năng (đã tách
    // xong dữ liệu, không dùng bản ghi tiềm năng nữa) — cả 5 lệnh ghi chạy trong 1 transaction,
    // lỗi giữa chừng rollback hết, không để lại bản ghi mồ côi.
    // existingCustomerId/existingContactId: dùng bản ghi đã có thay vì tạo mới (null = tạo mới,
    // chống trùng khách hàng khi người dùng chọn ghép vào KH sẵn có)
    public LeadResult convert(Long id, Long existingCustomerId, Long existingContactId) {
        return tx.call(() -> convertInTx(id, existingCustomerId, existingContactId));
    }

    private LeadResult convertInTx(Long id, Long existingCustomerId, Long existingContactId) {
        Lead lead = load(id);
        lead.getStatus().ensureCanTransitionTo(LeadStatus.converted);

        long now = System.currentTimeMillis();
        // Tiềm năng pool chung (chưa ai nhận) → người bấm convert trở thành người phụ trách 3 bản ghi mới
        Long ownerId = lead.getOwnerId() != null ? lead.getOwnerId() : currentUser.id();

        // khách hàng (mỏ neo B2B): dùng lại bản ghi người dùng đã chọn, hoặc tạo mới từ thông tin tổ chức
        Customer customer = existingCustomerId != null
                ? customerRepo.findById(existingCustomerId)
                        .orElseThrow(() -> new NotFoundException("Customer not found: " + existingCustomerId))
                : customerRepo.save(Customer.builder()
                        .code("KH-" + now)
                        .name(lead.getCompanyName() != null && !lead.getCompanyName().isBlank() ? lead.getCompanyName() : lead.getName())
                        .type(CustomerType.company)
                        .status(CustomerStatus.active)
                        .taxCode(lead.getTaxCode()).phone(lead.getPhone()).email(lead.getEmail())
                        .website(lead.getWebsite()).industry(lead.getIndustry()).source(lead.getSource())
                        .ownerId(ownerId)
                        .build());

        // liên hệ (cá nhân ra quyết định): dùng lại bản ghi đã chọn, hoặc tạo mới gắn vào khách hàng trên
        Contact contact = existingContactId != null
                ? contactRepo.findById(existingContactId)
                        .orElseThrow(() -> new NotFoundException("Contact not found: " + existingContactId))
                : contactRepo.save(Contact.builder()
                        .customerId(customer.getId())
                        .assignedUserId(ownerId)
                        .fullName(lead.getName()).title(lead.getTitle()).department(lead.getDepartment())
                        .email(lead.getEmail()).source(lead.getSource())
                        .doNotCall(lead.isDoNotCall()).doNotEmail(lead.isDoNotEmail())
                        .isPrimary(true)
                        .build());

        // cơ hội (giao dịch kỳ vọng đầu tiên) liên kết khách hàng + liên hệ vừa có
        BigDecimal estimated = lead.getEstimatedValue() != null ? lead.getEstimatedValue() : BigDecimal.ZERO;
        Opportunity opportunity = opportunityRepo.save(Opportunity.builder()
                .code("CH-" + now)
                .name("Cơ hội từ " + (lead.getCompanyName() != null && !lead.getCompanyName().isBlank() ? lead.getCompanyName() : lead.getName()))
                .opportunityType("KH mới")
                .customerId(customer.getId()).contactId(contact.getId()).ownerId(ownerId)
                .amount(estimated).expectedRevenue(estimated)
                .source(lead.getSource()).campaignId(lead.getCampaignId())
                .status(OpportunityStatus.open)
                .build());

        // chuyển converted + lưu liên kết tới 3 bản ghi vừa tạo
        Lead saved = repo.save(lead.toBuilder()
                .status(LeadStatus.converted)
                .customerId(customer.getId())
                .contactId(contact.getId())
                .convertedOpportunityId(opportunity.getId())
                .build());

        // xóa mềm tiềm năng — đã tách xong dữ liệu, giữ lại sẽ trùng lặp với KH/LH/CH vừa tạo
        repo.deleteById(saved.getId(), currentUser.id());
        return LeadCommandMapper.toResult(saved);
    }

    // đánh dấu đủ điều kiện thủ công (new/contacting -> qualified) — cần vì luồng tự động chỉ
    // qualify khi điểm vượt ngưỡng 50, sale thẩm định qua điện thoại thì không có cách nào khác
    // để mở khóa bước convert
    public LeadResult qualify(Long id) {
        Lead lead = load(id);
        lead.getStatus().ensureCanTransitionTo(LeadStatus.qualified);
        return LeadCommandMapper.toResult(repo.save(lead.toBuilder().status(LeadStatus.qualified).build()));
    }

    // đánh dấu thất bại (-> lost), ghi lý do vào ghi chú (reason có thể null)
    public LeadResult lose(Long id, String reason) {
        Lead lead = load(id);
        lead.getStatus().ensureCanTransitionTo(LeadStatus.lost);
        String note = reason == null || reason.isBlank() ? lead.getNote()
                : (lead.getNote() == null || lead.getNote().isBlank() ? "" : lead.getNote() + " | ") + "Lý do thất bại: " + reason;
        return LeadCommandMapper.toResult(repo.save(lead.toBuilder().status(LeadStatus.lost).note(note).build()));
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
            if (lead.getStatus() == LeadStatus.converted || lead.getStatus() == LeadStatus.lost) {
                throw new DomainException("Không thể nhận chăm sóc tiềm năng đã " +
                        (lead.getStatus() == LeadStatus.converted ? "chuyển đổi" : "đánh mất"));
            }
            return LeadCommandMapper.toResult(repo.save(lead.toBuilder().ownerId(userId).build()));
        });
    }

    private Lead load(Long id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Lead not found: " + id));
    }
}
