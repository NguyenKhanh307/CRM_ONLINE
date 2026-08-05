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
import vn.com.be_crm.core.error.frontend.DomainException;
import vn.com.be_crm.core.error.frontend.NotFoundException;
import vn.com.be_crm.core.tx.port.ITransactionRunner;

import java.util.List;
import java.util.stream.Collectors;

// xuất hóa đơn từ đơn hàng (Order-to-Invoice, quan hệ 1-1): sao chép sâu dòng hàng OrderItem ->
// InvoiceItem, gán invoice.orderId, khóa đơn hàng (audit trail) và chuyển đơn hàng sang Hoàn
// tất (completed)
public class CreateInvoiceFromOrderUseCase {
    private final IOrderRepository orderRepo;
    private final IOrderItemRepository orderItemRepo;
    private final IInvoiceRepository invoiceRepo;
    private final ITransactionRunner tx;

    public CreateInvoiceFromOrderUseCase(IOrderRepository orderRepo, IOrderItemRepository orderItemRepo,
                                         IInvoiceRepository invoiceRepo, ITransactionRunner tx) {
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
        this.invoiceRepo = invoiceRepo;
        this.tx = tx;
    }

    // tạo hóa đơn + dòng hàng và khóa đơn hàng chạy trong MỘT transaction
    public InvoiceResult execute(Long orderId) {
        return tx.call(() -> executeInTx(orderId));
    }

    // thân nghiệp vụ xuất hóa đơn — luôn chạy bên trong transaction
    private InvoiceResult executeInTx(Long orderId) {
        Order o = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));
        if (o.isLocked()) throw new DomainException("Đơn hàng đã xuất hóa đơn trước đó");

        // sao chép dòng hàng đơn hàng -> dòng hàng hóa đơn
        List<OrderItem> orderItems = orderItemRepo.findAllByOrderId(orderId);
        List<InvoiceItem> invItems = orderItems.stream().map(oi -> InvoiceItem.builder()
                .productId(oi.getProductId()).unit(oi.getUnit())
                .quantity(oi.getQuantity()).unitPrice(oi.getUnitPrice()).discount(oi.getDiscount())
                .taxRate(oi.getTaxRate()).note(oi.getNote())
                .build()).collect(Collectors.toList());

        Invoice invoice = Invoice.builder()
                .code("HD-" + System.currentTimeMillis())
                .orderId(o.getId())
                .ownerId(o.getOwnerId())
                .status(InvoiceStatus.draft).paymentStatus(PaymentStatus.unpaid)
                .build();
        Invoice savedInvoice = invoiceRepo.saveWithItems(invoice, invItems);

        // khóa đơn hàng + chuyển sang Hoàn tất (completed)
        Order locked = o.toBuilder().isLocked(true)
                .status(o.getStatus() != OrderStatus.completed ? OrderStatus.completed : o.getStatus())
                .build();
        orderRepo.save(locked);

        return InvoiceCommandMapper.toResult(savedInvoice);
    }
}
