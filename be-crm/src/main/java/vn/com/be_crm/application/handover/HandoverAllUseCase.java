package vn.com.be_crm.application.handover;

import vn.com.be_crm.application.shared.dto.HandoverAllCommand;
import vn.com.be_crm.domain.customer.repository.ICustomerRepository;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository;
import vn.com.be_crm.domain.order.repository.IOrderRepository;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;

/**
 * Use case bàn giao toàn bộ công việc từ một user sang user khác (5 module: lead, customer, opportunity, quotation, order).
 */
public class HandoverAllUseCase {

    private final ILeadRepository leadRepo;
    private final ICustomerRepository customerRepo;
    private final IOpportunityRepository opportunityRepo;
    private final IQuotationRepository quotationRepo;
    private final IOrderRepository orderRepo;

    /**
     * @param leadRepo         port lưu trữ Lead
     * @param customerRepo     port lưu trữ Customer
     * @param opportunityRepo  port lưu trữ Opportunity
     * @param quotationRepo    port lưu trữ Quotation
     * @param orderRepo        port lưu trữ Order
     */
    public HandoverAllUseCase(ILeadRepository leadRepo, ICustomerRepository customerRepo,
                               IOpportunityRepository opportunityRepo, IQuotationRepository quotationRepo,
                               IOrderRepository orderRepo) {
        this.leadRepo = leadRepo;
        this.customerRepo = customerRepo;
        this.opportunityRepo = opportunityRepo;
        this.quotationRepo = quotationRepo;
        this.orderRepo = orderRepo;
    }

    /**
     * Bàn giao toàn bộ 5 module từ fromUser sang toUser.
     * Dùng empty list để trigger bulk update với WHERE owner_id = fromUserId (không giới hạn IDs).
     *
     * @param cmd command chứa fromUserId, toUserId
     */
    public void execute(HandoverAllCommand cmd) {
        Long from = cmd.getFromUserId();
        Long to   = cmd.getToUserId();
        handoverAll(from, to);
    }

    /** Cập nhật owner_id toàn bộ bản ghi của fromUserId sang toUserId trên cả 5 bảng. */
    private void handoverAll(Long fromUserId, Long toUserId) {
        leadRepo.handoverAll(fromUserId, toUserId);
        customerRepo.handoverAll(fromUserId, toUserId);
        opportunityRepo.handoverAll(fromUserId, toUserId);
        quotationRepo.handoverAll(fromUserId, toUserId);
        orderRepo.handoverAll(fromUserId, toUserId);
    }
}
