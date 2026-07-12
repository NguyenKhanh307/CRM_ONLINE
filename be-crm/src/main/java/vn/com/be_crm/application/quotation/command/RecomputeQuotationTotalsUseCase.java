package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.application.shared.util.LineItemTotals;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.entity.QuotationItem;
import vn.com.be_crm.domain.quotation.repository.IQuotationItemRepository;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;

/**
 * Use case tính lại tổng tiền báo giá TỪ dòng hàng (server là nguồn chân lý, không tin số client gửi).
 * Gọi sau mỗi lần tạo/sửa/xóa dòng hàng báo giá và sau khi cập nhật header.
 */
public class RecomputeQuotationTotalsUseCase {
    private final IQuotationRepository repo;
    private final IQuotationItemRepository itemRepo;

    /** @param repo báo giá @param itemRepo dòng hàng báo giá */
    public RecomputeQuotationTotalsUseCase(IQuotationRepository repo, IQuotationItemRepository itemRepo) {
        this.repo = repo;
        this.itemRepo = itemRepo;
    }

    /**
     * Tính lại subtotal/discount/tax/total của báo giá theo dòng hàng hiện có rồi lưu.
     * @param quotationId ID báo giá
     */
    public void execute(Long quotationId) {
        if (quotationId == null) return;
        Quotation q = repo.findById(quotationId).orElse(null);
        if (q == null) return;
        LineItemTotals.Totals t = LineItemTotals.compute(
                itemRepo.findAllByQuotationId(quotationId),
                QuotationItem::getQuantity, QuotationItem::getUnitPrice,
                QuotationItem::getDiscount, QuotationItem::getTaxRate);
        repo.save(q.toBuilder()
                .subtotal(t.subtotal()).discount(t.discount()).tax(t.tax()).total(t.total())
                .build());
    }
}
