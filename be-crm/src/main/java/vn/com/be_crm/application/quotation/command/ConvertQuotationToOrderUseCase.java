package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.application.lead.command.NotifyLeadFirstOrderUseCase;
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

// chuyển báo giá thành đơn hàng (Quote-to-Order): sao chép sâu dòng hàng QLI -> OrderItem, khóa
// báo giá (audit trail), chuyển cơ hội liên quan sang Chốt Thắng (won)
public class ConvertQuotationToOrderUseCase {
    private final IQuotationRepository quotationRepo;
    private final IQuotationItemRepository quotationItemRepo;
    private final IOrderRepository orderRepo;
    private final IOpportunityRepository opportunityRepo;
    private final NotifyLeadFirstOrderUseCase notifyLeadFirstOrderUC;
    private final ITransactionRunner tx;

    public ConvertQuotationToOrderUseCase(IQuotationRepository quotationRepo, IQuotationItemRepository quotationItemRepo,
                                          IOrderRepository orderRepo, IOpportunityRepository opportunityRepo,
                                          NotifyLeadFirstOrderUseCase notifyLeadFirstOrderUC, ITransactionRunner tx) {
        this.quotationRepo = quotationRepo;
        this.quotationItemRepo = quotationItemRepo;
        this.orderRepo = orderRepo;
        this.opportunityRepo = opportunityRepo;
        this.notifyLeadFirstOrderUC = notifyLeadFirstOrderUC;
        this.tx = tx;
    }

    // tạo đơn + dòng hàng, khóa báo giá, chốt thắng cơ hội — tất cả trong MỘT transaction
    public OrderResult execute(Long quotationId) {
        return tx.call(() -> executeInTx(quotationId));
    }

    // thân nghiệp vụ convert — luôn chạy bên trong transaction
    private OrderResult executeInTx(Long quotationId) {
        Quotation q = quotationRepo.findById(quotationId)
                .orElseThrow(() -> new NotFoundException("Quotation not found: " + quotationId));
        if (q.isLocked()) throw new DomainException("Báo giá đã được chuyển thành đơn hàng trước đó");

        Opportunity opp = q.getOpportunityId() != null
                ? opportunityRepo.findById(q.getOpportunityId()).orElse(null) : null;

        // sao chép dòng hàng báo giá -> dòng hàng đơn hàng
        List<QuotationItem> qItems = quotationItemRepo.findAllByQuotationId(quotationId);
        List<OrderItem> orderItems = qItems.stream().map(qi -> OrderItem.builder()
                .productId(qi.getProductId()).unit(qi.getUnit())
                .quantity(qi.getQuantity()).unitPrice(qi.getUnitPrice()).discount(qi.getDiscount())
                .taxRate(qi.getTaxRate()).note(qi.getNote())
                .build()).collect(Collectors.toList());

        Order order = Order.builder()
                .code("DH-" + System.currentTimeMillis())
                .quotationId(q.getId())
                .ownerId(q.getOwnerId())
                .status(OrderStatus.draft)
                .build();
        Order savedOrder = orderRepo.saveWithItems(order, orderItems);

        // Khóa báo giá (read-only — dấu vết kiểm toán)
        quotationRepo.save(q.toBuilder().isLocked(true).build());

        // Chuyển cơ hội liên quan sang Chốt Thắng (won)
        if (opp != null && opp.getStatus() != OpportunityStatus.won) {
            opportunityRepo.save(opp.toBuilder().status(OpportunityStatus.won).build());
        }
        // đơn vừa tạo có phải đơn đầu tiên của tiềm năng nguồn (nếu có) không -> báo cân nhắc chuyển đổi
        notifyLeadFirstOrderUC.execute(q.getId(), savedOrder.getId());
        return OrderCommandMapper.toResult(savedOrder);
    }
}
