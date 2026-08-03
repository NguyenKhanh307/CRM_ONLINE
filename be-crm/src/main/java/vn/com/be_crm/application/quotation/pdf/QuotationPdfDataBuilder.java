package vn.com.be_crm.application.quotation.pdf;

import vn.com.be_crm.core.pdf.port.QuotationPdfData;
import vn.com.be_crm.domain.customer.entity.Customer;
import vn.com.be_crm.domain.customer.repository.ICustomerRepository;
import vn.com.be_crm.domain.product.entity.Product;
import vn.com.be_crm.domain.product.repository.IProductRepository;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.entity.QuotationItem;
import vn.com.be_crm.domain.quotation.repository.IQuotationItemRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Dựng {@link QuotationPdfData} từ một báo giá — dùng chung cho luồng gửi email
 * ({@code QuotationWorkflowUseCase.send}) và luồng xem trước PDF ({@code PreviewQuotationPdfUseCase}),
 * tránh trùng lặp logic gom tên khách hàng + dòng hàng.
 */
public class QuotationPdfDataBuilder {

    private final ICustomerRepository customerRepo;
    private final IQuotationItemRepository quotationItemRepo;
    private final IProductRepository productRepo;

    /** @param customerRepo khách hàng @param quotationItemRepo dòng hàng báo giá @param productRepo hàng hóa */
    public QuotationPdfDataBuilder(ICustomerRepository customerRepo, IQuotationItemRepository quotationItemRepo,
                                    IProductRepository productRepo) {
        this.customerRepo = customerRepo;
        this.quotationItemRepo = quotationItemRepo;
        this.productRepo = productRepo;
    }

    /**
     * Dựng dữ liệu PDF từ báo giá: tên KH, dòng hàng (kèm tên sản phẩm), tổng tiền.
     * @param q           báo giá
     * @param contactName tên người liên hệ hiển thị trên PDF
     * @return dữ liệu sẵn sàng đưa vào {@code IQuotationPdfService.render}
     */
    public QuotationPdfData build(Quotation q, String contactName) {
        String customerName = q.getCustomerId() == null ? "" :
                customerRepo.findById(q.getCustomerId()).map(Customer::getName).orElse("");
        List<QuotationPdfData.Line> lines = new ArrayList<>();
        int stt = 1;
        for (QuotationItem it : quotationItemRepo.findAllByQuotationId(q.getId())) {
            String productName = it.getProductId() == null ? "" :
                    productRepo.findById(it.getProductId()).map(Product::getName).orElse("#" + it.getProductId());
            lines.add(new QuotationPdfData.Line(stt++, productName, it.getUnit(),
                    it.getQuantity(), it.getUnitPrice(), it.getDiscount(), it.getAmount()));
        }
        return new QuotationPdfData(q.getCode(), customerName, contactName,
                q.getQuoteDate(), q.getValidUntil(), q.getCurrency(), q.getNote(), q.getTotal(), lines);
    }
}
