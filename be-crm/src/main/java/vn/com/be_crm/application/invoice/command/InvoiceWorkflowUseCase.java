package vn.com.be_crm.application.invoice.command;

import vn.com.be_crm.application.invoice.dto.InvoiceResult;
import vn.com.be_crm.application.invoice.mapper.InvoiceCommandMapper;
import vn.com.be_crm.domain.invoice.entity.Invoice;
import vn.com.be_crm.domain.invoice.entity.InvoicePaymentSchedule;
import vn.com.be_crm.domain.invoice.enums.InvoiceStatus;
import vn.com.be_crm.domain.invoice.enums.PaymentStatus;
import vn.com.be_crm.domain.invoice.repository.IInvoicePaymentScheduleRepository;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

import java.math.BigDecimal;

/**
 * Use case điều phối trạng thái hóa đơn (theo hành động, không sửa tay):
 * issue (draft → sent, khóa hóa đơn) / cancel (→ cancelled);
 * đồng thời suy ra trạng thái thanh toán từ các đợt thanh toán.
 */
public class InvoiceWorkflowUseCase {
    private final IInvoiceRepository invoiceRepo;
    private final IInvoicePaymentScheduleRepository scheduleRepo;

    /** @param invoiceRepo hóa đơn @param scheduleRepo đợt thanh toán */
    public InvoiceWorkflowUseCase(IInvoiceRepository invoiceRepo, IInvoicePaymentScheduleRepository scheduleRepo) {
        this.invoiceRepo = invoiceRepo;
        this.scheduleRepo = scheduleRepo;
    }

    /**
     * Phát hành hóa đơn (draft → sent) và khóa dữ liệu (read-only).
     * @param id ID hóa đơn @return hóa đơn sau cập nhật
     */
    public InvoiceResult issue(Long id) {
        Invoice o = load(id);
        o.getStatus().ensureCanTransitionTo(InvoiceStatus.sent);
        return InvoiceCommandMapper.toResult(invoiceRepo.save(
                o.toBuilder().status(InvoiceStatus.sent).isLocked(true).build()));
    }

    /**
     * Hủy hóa đơn (→ cancelled).
     * @param id ID hóa đơn @return hóa đơn sau cập nhật
     */
    public InvoiceResult cancel(Long id) {
        Invoice o = load(id);
        o.getStatus().ensureCanTransitionTo(InvoiceStatus.cancelled);
        return InvoiceCommandMapper.toResult(invoiceRepo.save(o.toBuilder().status(InvoiceStatus.cancelled).build()));
    }

    /**
     * Tính lại trạng thái thanh toán & trạng thái hóa đơn từ tổng tiền đã trả của các đợt thanh toán.
     * Gọi sau mỗi lần tạo/sửa/xóa đợt thanh toán.
     * @param invoiceId ID hóa đơn
     */
    public void recalcPaymentStatus(Long invoiceId) {
        Invoice o = invoiceRepo.findById(invoiceId).orElse(null);
        if (o == null) return;
        BigDecimal paid = scheduleRepo.findAllByInvoiceId(invoiceId).stream()
                .map(InvoicePaymentSchedule::getPaidAmount)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        PaymentStatus newPay = PaymentStatus.fromAmounts(paid, o.getTotal());
        // Suy ra trạng thái hóa đơn theo mức thanh toán (chỉ khi đã phát hành, chưa hủy)
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

    /** Tải hóa đơn theo ID hoặc ném NotFoundException. */
    private Invoice load(Long id) {
        return invoiceRepo.findById(id).orElseThrow(() -> new NotFoundException("Invoice not found: " + id));
    }
}
