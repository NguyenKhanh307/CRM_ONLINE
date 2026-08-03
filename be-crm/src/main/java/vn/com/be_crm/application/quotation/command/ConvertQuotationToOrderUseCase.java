package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.application.order.dto.OrderResult;
import vn.com.be_crm.application.order.mapper.OrderCommandMapper;
import vn.com.be_crm.domain.opportunity.entity.Opportunity;
import vn.com.be_crm.domain.opportunity.enums.OpportunityStatus;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository;
import vn.com.be_crm.domain.order.entity.Order;
import vn.com.be_crm.domain.order.entity.OrderItem;
import vn.com.be_crm.domain.order.enums.OrderStatus;
import vn.com.be_crm.domain.order.repository.IOrderRepository;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.entity.QuotationItem;
import vn.com.be_crm.domain.quotation.repository.IQuotationItemRepository;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;
import vn.com.be_crm.core.error.frontend.DomainException;
import vn.com.be_crm.core.error.frontend.NotFoundException;
import vn.com.be_crm.core.tx.port.ITransactionRunner;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Use case chuyển báo giá thành đơn hàng (Quote-to-Order):
 * sao chép sâu dòng hàng QLI → OrderItem, khóa báo giá (audit trail),
 * kế thừa chiến dịch (attribution) từ cơ hội, và chuyển cơ hội liên quan sang Chốt Thắng (won).
 */
public class ConvertQuotationToOrderUseCase {
    private final IQuotationRepository quotationRepo;
    private final IQuotationItemRepository quotationItemRepo;
    private final IOrderRepository orderRepo;
    private final IOpportunityRepository opportunityRepo;
    private final ITransactionRunner tx;

    /** @param quotationRepo báo giá @param quotationItemRepo dòng báo giá @param orderRepo đơn hàng @param opportunityRepo cơ hội @param tx bộ chạy transaction */
    public ConvertQuotationToOrderUseCase(IQuotationRepository quotationRepo, IQuotationItemRepository quotationItemRepo,
                                          IOrderRepository orderRepo, IOpportunityRepository opportunityRepo,
                                          ITransactionRunner tx) {
        this.quotationRepo = quotationRepo;
        this.quotationItemRepo = quotationItemRepo;
        this.orderRepo = orderRepo;
        this.opportunityRepo = opportunityRepo;
        this.tx = tx;
    }

    /**
     * Chuyển báo giá thành đơn hàng.
     * Tạo đơn + dòng hàng, khóa báo giá, chốt thắng cơ hội — tất cả trong MỘT transaction.
     * @param quotationId ID báo giá @return đơn hàng vừa tạo
     */
    public OrderResult execute(Long quotationId) {
        return tx.call(() -> executeInTx(quotationId));
    }

    /** Thân nghiệp vụ convert — luôn chạy bên trong transaction. */
    private OrderResult executeInTx(Long quotationId) {
        Quotation q = quotationRepo.findById(quotationId)
                .orElseThrow(() -> new NotFoundException("Quotation not found: " + quotationId));
        if (q.isLocked()) throw new DomainException("Báo giá đã được chuyển thành đơn hàng trước đó");

        // Kế thừa chiến dịch (attribution): ưu tiên chiến dịch lưu trực tiếp trên báo giá,
        // fallback tra cơ hội liên quan (tương thích dữ liệu cũ chưa có campaign_id trên báo giá)
        Opportunity opp = q.getOpportunityId() != null
                ? opportunityRepo.findById(q.getOpportunityId()).orElse(null) : null;
        Long campaignId = q.getCampaignId() != null ? q.getCampaignId()
                : (opp != null ? opp.getCampaignId() : null);

        // Sao chép dòng hàng báo giá → dòng hàng đơn hàng
        List<QuotationItem> qItems = quotationItemRepo.findAllByQuotationId(quotationId);
        List<OrderItem> orderItems = qItems.stream().map(qi -> OrderItem.builder()
                .productId(qi.getProductId()).unit(qi.getUnit())
                .quantity(qi.getQuantity()).unitPrice(qi.getUnitPrice()).discount(qi.getDiscount())
                .taxRate(qi.getTaxRate()).amount(qi.getAmount()).note(qi.getNote())
                .build()).collect(Collectors.toList());

        Order order = Order.builder()
                .code("DH-" + System.currentTimeMillis())
                .customerId(q.getCustomerId()).contactId(q.getContactId())
                .quotationId(q.getId()).opportunityId(q.getOpportunityId())
                .campaignId(campaignId)
                .ownerId(q.getOwnerId())
                .currency(q.getCurrency()).exchangeRate(q.getExchangeRate())
                .status(OrderStatus.draft)
                .subtotal(q.getSubtotal()).discount(q.getDiscount()).tax(q.getTax()).total(q.getTotal())
                .build();
        Order savedOrder = orderRepo.saveWithItems(order, orderItems);

        // Khóa báo giá (read-only — dấu vết kiểm toán)
        quotationRepo.save(q.toBuilder().isLocked(true).build());

        // Chuyển cơ hội liên quan sang Chốt Thắng (won)
        if (opp != null && opp.getStatus() != OpportunityStatus.won) {
            opportunityRepo.save(opp.toBuilder().status(OpportunityStatus.won).build());
        }
        return OrderCommandMapper.toResult(savedOrder);
    }
}
