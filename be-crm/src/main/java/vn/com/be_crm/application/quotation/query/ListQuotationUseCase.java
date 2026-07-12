package vn.com.be_crm.application.quotation.query;

import vn.com.be_crm.application.quotation.dto.QuotationResult;
import vn.com.be_crm.application.quotation.mapper.QuotationCommandMapper;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.application.shared.lookup.INameResolver;
import vn.com.be_crm.application.shared.lookup.NameEnricher;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy danh sách báo giá có phân trang. */
public class ListQuotationUseCase implements IUseCase<PageRequest, PageResult<QuotationResult>> {
    private final IQuotationRepository repo;
    private final INameResolver names;
    /** @param repo port lưu trữ @param names port tra tên khóa ngoại */
    public ListQuotationUseCase(IQuotationRepository repo, INameResolver names) { this.repo = repo; this.names = names; }
    /** Lấy danh sách Quotation kèm tên khóa ngoại (khách hàng, liên hệ, cơ hội, người phụ trách). @param r phân trang @return PageResult */
    @Override public PageResult<QuotationResult> execute(PageRequest r) {
        var page = repo.findAll(r);
        List<QuotationResult> items = page.getItems().stream().map(QuotationCommandMapper::toResult).collect(Collectors.toList());
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
