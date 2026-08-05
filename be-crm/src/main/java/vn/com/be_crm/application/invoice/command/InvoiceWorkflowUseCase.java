package vn.com.be_crm.application.invoice.command;

import vn.com.be_crm.application.invoice.dto.InvoiceResult;
import vn.com.be_crm.application.invoice.mapper.InvoiceCommandMapper;
import vn.com.be_crm.core.util.LineItemTotals;
import vn.com.be_crm.domain.invoice.entity.Invoice;
import vn.com.be_crm.domain.invoice.entity.InvoiceItem;
import vn.com.be_crm.domain.invoice.entity.InvoicePaymentSchedule;
import vn.com.be_crm.domain.invoice.enums.InvoiceStatus;
import vn.com.be_crm.domain.invoice.enums.PaymentStatus;
import vn.com.be_crm.domain.invoice.repository.IInvoiceItemRepository;
import vn.com.be_crm.domain.invoice.repository.IInvoicePaymentScheduleRepository;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

import java.math.BigDecimal;

// điều phối trạng thái hóa đơn (theo hành động, không sửa tay): issue (draft -> sent, khóa hóa
// đơn) / cancel (-> cancelled); đồng thời suy ra trạng thái thanh toán từ các đợt thanh toán
public class InvoiceWorkflowUseCase {
    private final IInvoiceRepository invoiceRepo;
    private final IInvoiceItemRepository itemRepo;
    private final IInvoicePaymentScheduleRepository scheduleRepo;

    public InvoiceWorkflowUseCase(IInvoiceRepository invoiceRepo, IInvoiceItemRepository itemRepo,
                                   IInvoicePaymentScheduleRepository scheduleRepo) {
        this.invoiceRepo = invoiceRepo;
        this.itemRepo = itemRepo;
        this.scheduleRepo = scheduleRepo;
    }

    // phát hành hóa đơn (draft -> sent) và khóa dữ liệu (read-only)
    public InvoiceResult issue(Long id) {
        Invoice o = load(id);
        o.getStatus().ensureCanTransitionTo(InvoiceStatus.sent);
        return InvoiceCommandMapper.toResult(invoiceRepo.save(
                o.toBuilder().status(InvoiceStatus.sent).isLocked(true).build()));
    }

    // hủy hóa đơn (-> cancelled)
    public InvoiceResult cancel(Long id) {
        Invoice o = load(id);
        o.getStatus().ensureCanTransitionTo(InvoiceStatus.cancelled);
        return InvoiceCommandMapper.toResult(invoiceRepo.save(o.toBuilder().status(InvoiceStatus.cancelled).build()));
    }

    // tính lại trạng thái thanh toán & trạng thái hóa đơn từ tổng tiền đã trả của các đợt thanh
    // toán so với tổng hóa đơn (tính từ dòng hàng, không còn cột lưu sẵn). Gọi sau mỗi lần tạo/
    // sửa/xóa đợt thanh toán.
    public void recalcPaymentStatus(Long invoiceId) {
        Invoice o = invoiceRepo.findById(invoiceId).orElse(null);
        if (o == null) return;
        BigDecimal paid = scheduleRepo.findAllByInvoiceId(invoiceId).stream()
                .map(InvoicePaymentSchedule::getPaidAmount)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        java.util.List<InvoiceItem> items = itemRepo.findAllByInvoiceId(invoiceId);
        BigDecimal total = LineItemTotals.compute(items,
                InvoiceItem::getQuantity, InvoiceItem::getUnitPrice, InvoiceItem::getDiscount, InvoiceItem::getTaxRate).total();
        PaymentStatus newPay = PaymentStatus.fromAmounts(paid, total);
        // suy ra trạng thái hóa đơn theo mức thanh toán (chỉ khi đã phát hành, chưa hủy)
        InvoiceStatus newStatus = o.getStatus();
        if (o.getStatus() == InvoiceStatus.sent || o.getStatus() == InvoiceStatus.partially_paid
                || o.getStatus() == InvoiceStatus.paid) {
            newStatus = switch (newPay) {
                case paid -> InvoiceStatus.paid;
                case partial -> InvoiceStatus.partially_paid;
                case unpaid -> InvoiceStatus.sent;
            };
        }
        if (newPay != o.getPaymentStatus() || newStatus != o.getStatus()) {
            invoiceRepo.save(o.toBuilder().paymentStatus(newPay).status(newStatus).build());
        }
    }

    private Invoice load(Long id) {
        return invoiceRepo.findById(id).orElseThrow(() -> new NotFoundException("Invoice not found: " + id));
    }
}
