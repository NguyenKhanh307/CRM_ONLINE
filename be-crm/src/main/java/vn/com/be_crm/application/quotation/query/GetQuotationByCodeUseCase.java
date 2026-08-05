package vn.com.be_crm.application.quotation.query;

import vn.com.be_crm.application.quotation.dto.PublicQuotationView;
import vn.com.be_crm.core.util.LineItemTotals;
import vn.com.be_crm.domain.contact.repository.IContactRepository;
import vn.com.be_crm.domain.customer.repository.ICustomerRepository;
import vn.com.be_crm.domain.product.entity.Product;
import vn.com.be_crm.domain.product.repository.IProductRepository;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.entity.QuotationItem;
import vn.com.be_crm.domain.quotation.repository.IQuotationItemRepository;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

import java.util.ArrayList;
import java.util.List;

// lấy báo giá theo mã (code) để hiển thị trên trang phản hồi công khai của khách hàng — mã báo
// giá không bí mật, giống cách phiếu hỗ trợ dùng code làm định danh
public class GetQuotationByCodeUseCase {
    private final IQuotationRepository quotationRepo;
    private final IQuotationItemRepository itemRepo;
    private final IProductRepository productRepo;
    private final ICustomerRepository customerRepo;
    private final IContactRepository contactRepo;

    public GetQuotationByCodeUseCase(IQuotationRepository quotationRepo, IQuotationItemRepository itemRepo,
                                      IProductRepository productRepo, ICustomerRepository customerRepo,
                                      IContactRepository contactRepo) {
        this.quotationRepo = quotationRepo;
        this.itemRepo = itemRepo;
        this.productRepo = productRepo;
        this.customerRepo = customerRepo;
        this.contactRepo = contactRepo;
    }

    public PublicQuotationView execute(String code) {
        Quotation q = quotationRepo.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy báo giá"));

        String customerName = q.getCustomerId() == null ? "" :
                customerRepo.findById(q.getCustomerId()).map(c -> c.getName()).orElse("");
        String contactName = q.getContactId() == null ? "" :
                contactRepo.findById(q.getContactId()).map(c -> c.getFullName()).orElse("");

        List<QuotationItem> qItems = itemRepo.findAllByQuotationId(q.getId());
        List<PublicQuotationView.Line> items = new ArrayList<>();
        for (QuotationItem it : qItems) {
            String productName = it.getProductId() == null ? "" :
                    productRepo.findById(it.getProductId()).map(Product::getName).orElse("#" + it.getProductId());
            items.add(new PublicQuotationView.Line(productName, it.getUnit(),
                    it.getQuantity(), it.getUnitPrice(), it.getDiscount(),
                    LineItemTotals.lineAmount(it.getQuantity(), it.getUnitPrice(), it.getDiscount(), it.getTaxRate())));
        }
        LineItemTotals.Totals totals = LineItemTotals.compute(qItems,
                QuotationItem::getQuantity, QuotationItem::getUnitPrice, QuotationItem::getDiscount, QuotationItem::getTaxRate);

        return new PublicQuotationView(q.getCode(), customerName, contactName,
                q.getQuoteDate(), q.getValidUntil(), q.getNote(),
                totals.subtotal(), totals.discount(), totals.tax(), totals.total(),
                q.getStatus() != null ? q.getStatus().name() : null,
                q.getCustomerResponse(), q.getCustomerResponseNote(), items);
    }
}
