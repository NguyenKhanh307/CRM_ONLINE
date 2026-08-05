package vn.com.be_crm.application.quotation.pdf;

import vn.com.be_crm.core.pdf.port.QuotationPdfData;
import vn.com.be_crm.core.util.LineItemTotals;
import vn.com.be_crm.domain.customer.entity.Customer;
import vn.com.be_crm.domain.customer.repository.ICustomerRepository;
import vn.com.be_crm.domain.product.entity.Product;
import vn.com.be_crm.domain.product.repository.IProductRepository;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.entity.QuotationItem;
import vn.com.be_crm.domain.quotation.repository.IQuotationItemRepository;

import java.util.ArrayList;
import java.util.List;

// dựng QuotationPdfData từ một báo giá — dùng chung cho luồng gửi email
// (QuotationWorkflowUseCase.send) và luồng xem trước PDF (PreviewQuotationPdfUseCase), tránh
// trùng lặp logic gom tên khách hàng + dòng hàng
public class QuotationPdfDataBuilder {

    private final ICustomerRepository customerRepo;
    private final IQuotationItemRepository quotationItemRepo;
    private final IProductRepository productRepo;

    public QuotationPdfDataBuilder(ICustomerRepository customerRepo, IQuotationItemRepository quotationItemRepo,
                                    IProductRepository productRepo) {
        this.customerRepo = customerRepo;
        this.quotationItemRepo = quotationItemRepo;
        this.productRepo = productRepo;
    }

    // dựng dữ liệu PDF từ báo giá: tên KH, dòng hàng (kèm tên sản phẩm), tổng tiền — tất cả tính
    // từ dòng hàng, không còn cột lưu sẵn trên Quotation
    public QuotationPdfData build(Quotation q, String contactName) {
        String customerName = q.getCustomerId() == null ? "" :
                customerRepo.findById(q.getCustomerId()).map(Customer::getName).orElse("");
        List<QuotationItem> items = quotationItemRepo.findAllByQuotationId(q.getId());
        List<QuotationPdfData.Line> lines = new ArrayList<>();
        int stt = 1;
        for (QuotationItem it : items) {
            String productName = it.getProductId() == null ? "" :
                    productRepo.findById(it.getProductId()).map(Product::getName).orElse("#" + it.getProductId());
            lines.add(new QuotationPdfData.Line(stt++, productName, it.getUnit(),
                    it.getQuantity(), it.getUnitPrice(), it.getDiscount(),
                    LineItemTotals.lineAmount(it.getQuantity(), it.getUnitPrice(), it.getDiscount(), it.getTaxRate())));
        }
        LineItemTotals.Totals t = LineItemTotals.compute(items,
                QuotationItem::getQuantity, QuotationItem::getUnitPrice, QuotationItem::getDiscount, QuotationItem::getTaxRate);
        return new QuotationPdfData(q.getCode(), customerName, contactName,
                q.getQuoteDate(), q.getValidUntil(), q.getNote(), t.total(), lines);
    }
}
