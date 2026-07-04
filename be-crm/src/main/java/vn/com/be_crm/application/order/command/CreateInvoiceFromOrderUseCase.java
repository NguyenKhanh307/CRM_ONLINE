package vn.com.be_crm.application.order.command;

import vn.com.be_crm.application.invoice.dto.InvoiceResult;
import vn.com.be_crm.application.invoice.mapper.InvoiceCommandMapper;
import vn.com.be_crm.domain.invoice.entity.Invoice;
import vn.com.be_crm.domain.invoice.entity.InvoiceItem;
import vn.com.be_crm.domain.invoice.enums.InvoiceStatus;
import vn.com.be_crm.domain.invoice.enums.PaymentStatus;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRepository;
import vn.com.be_crm.domain.order.entity.Order;
import vn.com.be_crm.domain.order.entity.OrderItem;
import vn.com.be_crm.domain.order.enums.OrderStatus;
import vn.com.be_crm.domain.order.repository.IOrderItemRepository;
import vn.com.be_crm.domain.order.repository.IOrderRepository;
import vn.com.be_crm.domain.shared.exception.DomainException;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Use case xuất hóa đơn từ đơn hàng (Order-to-Invoice, quan hệ 1-1):
 * sao chép sâu dòng hàng OrderItem → InvoiceItem, gán invoice.orderId + kế thừa chiến dịch,
 * khóa đơn hàng (audit trail) và chuyển đơn hàng sang Hoàn tất (completed).
 */
public class CreateInvoiceFromOrderUseCase {
    private final IOrderRepository orderRepo;
    private final IOrderItemRepository orderItemRepo;
    private final IInvoiceRepository invoiceRepo;

    /** @param orderRepo đơn hàng @param orderItemRepo dòng đơn hàng @param invoiceRepo hóa đơn */
    public CreateInvoiceFromOrderUseCase(IOrderRepository orderRepo, IOrderItemRepository orderItemRepo,
                                         IInvoiceRepository invoiceRepo) {
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
        this.invoiceRepo = invoiceRepo;
    }

    /**
     * Xuất hóa đơn từ đơn hàng.
     * @param orderId ID đơn hàng @return hóa đơn vừa tạo
     */
    public InvoiceResult execute(Long orderId) {
        Order o = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));
        if (o.isLocked()) throw new DomainException("Đơn hàng đã xuất hóa đơn trước đó");

        // Sao chép dòng hàng đơn hàng → dòng hàng hóa đơn
        List<OrderItem> orderItems = orderItemRepo.findAllByOrderId(orderId);
        List<InvoiceItem> invItems = orderItems.stream().map(oi -> InvoiceItem.builder()
                .productId(oi.getProductId()).unit(oi.getUnit())
                .quantity(oi.getQuantity()).unitPrice(oi.getUnitPrice()).discount(oi.getDiscount())
                .taxRate(oi.getTaxRate()).amount(oi.getAmount()).note(oi.getNote())
                .build()).collect(Collectors.toList());

        Invoice invoice = Invoice.builder()
                .code("HD-" + System.currentTimeMillis())
                .customerId(o.getCustomerId()).contactId(o.getContactId())
                .quotationId(o.getQuotationId()).opportunityId(o.getOpportunityId())
                .orderId(o.getId()).campaignId(o.getCampaignId())
                .ownerId(o.getOwnerId())
                .currency(o.getCurrency()).exchangeRate(o.getExchangeRate())
                .status(InvoiceStatus.draft).paymentStatus(PaymentStatus.unpaid)
                .billingAddress(o.getBillingAddress()).taxCode(o.getTaxCode())
                .subtotal(o.getSubtotal()).discount(o.getDiscount()).tax(o.getTax()).total(o.getTotal())
                .build();
        Invoice savedInvoice = invoiceRepo.saveWithItems(invoice, invItems);

        // Khóa đơn hàng + chuyển sang Hoàn tất (completed)
        Order locked = o.toBuilder().isLocked(true)
                .status(o.getStatus() != OrderStatus.completed ? OrderStatus.completed : o.getStatus())
                .build();
        orderRepo.save(locked);

        return InvoiceCommandMapper.toResult(savedInvoice);
    }
}
