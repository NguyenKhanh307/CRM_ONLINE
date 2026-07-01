package vn.com.be_crm.application.quotation.query;

import vn.com.be_crm.application.quotation.dto.PublicQuotationView;
import vn.com.be_crm.domain.contact.repository.IContactRepository;
import vn.com.be_crm.domain.customer.repository.ICustomerRepository;
import vn.com.be_crm.domain.product.entity.Product;
import vn.com.be_crm.domain.product.repository.IProductRepository;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.entity.QuotationItem;
import vn.com.be_crm.domain.quotation.repository.IQuotationItemRepository;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * Use case lấy báo giá theo token để hiển thị trên trang phản hồi công khai của khách hàng.
 */
public class GetQuotationByTokenUseCase {
    private final IQuotationRepository quotationRepo;
    private final IQuotationItemRepository itemRepo;
    private final IProductRepository productRepo;
    private final ICustomerRepository customerRepo;
    private final IContactRepository contactRepo;

    /** @param quotationRepo báo giá @param itemRepo dòng hàng @param productRepo hàng hóa @param customerRepo khách hàng @param contactRepo liên hệ */
    public GetQuotationByTokenUseCase(IQuotationRepository quotationRepo, IQuotationItemRepository itemRepo,
                                      IProductRepository productRepo, ICustomerRepository customerRepo,
                                      IContactRepository contactRepo) {
        this.quotationRepo = quotationRepo;
        this.itemRepo = itemRepo;
        this.productRepo = productRepo;
        this.customerRepo = customerRepo;
        this.contactRepo = contactRepo;
    }

    /**
     * Lấy thông tin báo giá công khai theo token.
     * @param token token phản hồi @return PublicQuotationView
     */
    public PublicQuotationView execute(String token) {
        Quotation q = quotationRepo.findByResponseToken(token)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy báo giá hoặc liên kết đã hết hạn"));

        String customerName = q.getCustomerId() == null ? "" :
                customerRepo.findById(q.getCustomerId()).map(c -> c.getName()).orElse("");
        String contactName = q.getContactId() == null ? "" :
                contactRepo.findById(q.getContactId()).map(c -> c.getFullName()).orElse("");

        List<PublicQuotationView.Line> items = new ArrayList<>();
        for (QuotationItem it : itemRepo.findAllByQuotationId(q.getId())) {
            String productName = it.getProductId() == null ? "" :
                    productRepo.findById(it.getProductId()).map(Product::getName).orElse("#" + it.getProductId());
            items.add(new PublicQuotationView.Line(productName, it.getUnit(),
                    it.getQuantity(), it.getUnitPrice(), it.getDiscount(), it.getAmount()));
        }

        return new PublicQuotationView(q.getCode(), customerName, contactName,
                q.getQuoteDate(), q.getValidUntil(), q.getCurrency(), q.getNote(),
                q.getSubtotal(), q.getDiscount(), q.getTax(), q.getTotal(),
                q.getStatus() != null ? q.getStatus().name() : null,
                q.getCustomerResponse(), q.getCustomerResponseNote(), items);
    }
}
