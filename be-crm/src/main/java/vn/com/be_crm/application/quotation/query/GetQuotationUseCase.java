package vn.com.be_crm.application.quotation.query;

import vn.com.be_crm.application.quotation.dto.QuotationResult;
import vn.com.be_crm.application.quotation.mapper.QuotationCommandMapper;
import vn.com.be_crm.core.lookup.port.INameResolver;
import vn.com.be_crm.core.lookup.NameEnricher;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.core.util.LineItemTotals;
import vn.com.be_crm.domain.quotation.entity.QuotationItem;
import vn.com.be_crm.domain.quotation.repository.IQuotationItemRepository;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

import java.util.List;

// lấy báo giá theo ID — kèm tên khóa ngoại + tổng tiền tính từ dòng hàng (không còn cột lưu sẵn)
public class GetQuotationUseCase implements IUseCase<Long, QuotationResult> {
    private final IQuotationRepository repo;
    private final IQuotationItemRepository itemRepo;
    private final INameResolver names;
    private final QuotationExpiryUseCase expiryUC;

    public GetQuotationUseCase(IQuotationRepository repo, IQuotationItemRepository itemRepo, INameResolver names,
                                QuotationExpiryUseCase expiryUC) {
        this.repo = repo;
        this.itemRepo = itemRepo;
        this.names = names;
        this.expiryUC = expiryUC;
    }

    @Override public QuotationResult execute(Long id) {
        var e = repo.findById(id).orElseThrow(() -> new NotFoundException("Quotation not found: " + id));
        e = expiryUC.checkAndExpire(e);
        QuotationResult result = QuotationCommandMapper.toResult(e);
        List<QuotationItem> items = itemRepo.findAllByQuotationId(id);
        LineItemTotals.Totals t = LineItemTotals.compute(items,
                QuotationItem::getQuantity, QuotationItem::getUnitPrice, QuotationItem::getDiscount, QuotationItem::getTaxRate);
        result.setSubtotal(t.subtotal()); result.setDiscount(t.discount());
        result.setTax(t.tax()); result.setTotal(t.total());

        List<QuotationResult> one = List.of(result);
        NameEnricher.apply(one, QuotationResult::getCustomerId, names::customers, QuotationResult::setCustomerName);
        NameEnricher.apply(one, QuotationResult::getContactId, names::contacts, QuotationResult::setContactName);
        NameEnricher.apply(one, QuotationResult::getOpportunityId, names::opportunities, QuotationResult::setOpportunityName);
        NameEnricher.apply(one, QuotationResult::getOwnerId, names::users, QuotationResult::setOwnerName);
        NameEnricher.apply(one, QuotationResult::getCreatedBy, names::users, QuotationResult::setCreatedByName);
        NameEnricher.apply(one, QuotationResult::getUpdatedBy, names::users, QuotationResult::setUpdatedByName);
        return result;
    }
}
