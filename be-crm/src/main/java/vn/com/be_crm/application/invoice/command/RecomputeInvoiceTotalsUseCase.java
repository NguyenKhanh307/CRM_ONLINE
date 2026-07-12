package vn.com.be_crm.application.invoice.command;

import vn.com.be_crm.application.shared.util.LineItemTotals;
import vn.com.be_crm.domain.invoice.entity.Invoice;
import vn.com.be_crm.domain.invoice.entity.InvoiceItem;
import vn.com.be_crm.domain.invoice.repository.IInvoiceItemRepository;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRepository;

/**
 * Use case tính lại tổng tiền hóa đơn TỪ dòng hàng (server là nguồn chân lý, không tin số client gửi).
 * Gọi sau mỗi lần tạo/sửa/xóa dòng hàng hóa đơn và sau khi cập nhật header.
 */
public class RecomputeInvoiceTotalsUseCase {
    private final IInvoiceRepository repo;
    private final IInvoiceItemRepository itemRepo;

    /** @param repo hóa đơn @param itemRepo dòng hàng hóa đơn */
    public RecomputeInvoiceTotalsUseCase(IInvoiceRepository repo, IInvoiceItemRepository itemRepo) {
        this.repo = repo;
        this.itemRepo = itemRepo;
    }

    /**
     * Tính lại subtotal/discount/tax/total của hóa đơn theo dòng hàng hiện có rồi lưu.
     * @param invoiceId ID hóa đơn
     */
    public void execute(Long invoiceId) {
        if (invoiceId == null) return;
        Invoice inv = repo.findById(invoiceId).orElse(null);
        if (inv == null) return;
        LineItemTotals.Totals t = LineItemTotals.compute(
                itemRepo.findAllByInvoiceId(invoiceId),
                InvoiceItem::getQuantity, InvoiceItem::getUnitPrice,
                InvoiceItem::getDiscount, InvoiceItem::getTaxRate);
        repo.save(inv.toBuilder()
                .subtotal(t.subtotal()).discount(t.discount()).tax(t.tax()).total(t.total())
                .build());
    }
}
