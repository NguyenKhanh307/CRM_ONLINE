package vn.com.be_crm.application.quotation.query;

import vn.com.be_crm.application.quotation.dto.QuotationResult;
import vn.com.be_crm.application.quotation.mapper.QuotationCommandMapper;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.page.PageResult;
import vn.com.be_crm.core.lookup.port.INameResolver;
import vn.com.be_crm.core.lookup.NameEnricher;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.core.util.LineItemTotals;
import vn.com.be_crm.domain.quotation.entity.QuotationItem;
import vn.com.be_crm.domain.quotation.repository.IQuotationItemRepository;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;

import java.util.List;
import java.util.stream.Collectors;

// lấy danh sách báo giá có phân trang, kèm tên khóa ngoại + tổng tiền tính từ dòng hàng của
// từng bản ghi (không còn cột lưu sẵn — chấp nhận đánh đổi thêm 1 truy vấn/dòng ở quy mô dữ liệu
// hiện tại)
public class ListQuotationUseCase implements IUseCase<PageRequest, PageResult<QuotationResult>> {
    private final IQuotationRepository repo;
    private final IQuotationItemRepository itemRepo;
    private final INameResolver names;

    public ListQuotationUseCase(IQuotationRepository repo, IQuotationItemRepository itemRepo, INameResolver names) {
        this.repo = repo; this.itemRepo = itemRepo; this.names = names;
    }

    @Override public PageResult<QuotationResult> execute(PageRequest r) {
        var page = repo.findAll(r);
        List<QuotationResult> items = page.getItems().stream().map(QuotationCommandMapper::toResult).collect(Collectors.toList());
        for (QuotationResult res : items) {
            List<QuotationItem> qItems = itemRepo.findAllByQuotationId(res.getId());
            LineItemTotals.Totals t = LineItemTotals.compute(qItems,
                    QuotationItem::getQuantity, QuotationItem::getUnitPrice, QuotationItem::getDiscount, QuotationItem::getTaxRate);
            res.setSubtotal(t.subtotal()); res.setDiscount(t.discount());
            res.setTax(t.tax()); res.setTotal(t.total());
        }
        NameEnricher.apply(items, QuotationResult::getCustomerId, names::customers, QuotationResult::setCustomerName);
        NameEnricher.apply(items, QuotationResult::getContactId, names::contacts, QuotationResult::setContactName);
        NameEnricher.apply(items, QuotationResult::getOpportunityId, names::opportunities, QuotationResult::setOpportunityName);
        NameEnricher.apply(items, QuotationResult::getOwnerId, names::users, QuotationResult::setOwnerName);
        NameEnricher.apply(items, QuotationResult::getCreatedBy, names::users, QuotationResult::setCreatedByName);
        NameEnricher.apply(items, QuotationResult::getUpdatedBy, names::users, QuotationResult::setUpdatedByName);
        return PageResult.<QuotationResult>builder()
                .items(items)
                .total(page.getTotal()).page(page.getPage()).size(page.getSize()).build();
    }
}
