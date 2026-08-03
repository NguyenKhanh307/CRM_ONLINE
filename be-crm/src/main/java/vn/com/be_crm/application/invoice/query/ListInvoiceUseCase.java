package vn.com.be_crm.application.invoice.query;

import vn.com.be_crm.application.invoice.dto.InvoiceResult;
import vn.com.be_crm.application.invoice.mapper.InvoiceCommandMapper;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.page.PageResult;
import vn.com.be_crm.core.lookup.port.INameResolver;
import vn.com.be_crm.core.lookup.NameEnricher;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy danh sách đơn hàng có phân trang. */
public class ListInvoiceUseCase implements IUseCase<PageRequest, PageResult<InvoiceResult>> {
    private final IInvoiceRepository repo;
    private final INameResolver names;
    /** @param repo port lưu trữ @param names port tra tên khóa ngoại */
    public ListInvoiceUseCase(IInvoiceRepository repo, INameResolver names) { this.repo = repo; this.names = names; }
    /** Lấy danh sách Invoice kèm tên khóa ngoại (khách hàng, liên hệ, báo giá, cơ hội, người phụ trách). @param r phân trang @return PageResult */
    @Override public PageResult<InvoiceResult> execute(PageRequest r) {
        var page = repo.findAll(r);
        List<InvoiceResult> items = page.getItems().stream().map(InvoiceCommandMapper::toResult).collect(Collectors.toList());
        NameEnricher.apply(items, InvoiceResult::getCustomerId, names::customers, InvoiceResult::setCustomerName);
        NameEnricher.apply(items, InvoiceResult::getContactId, names::contacts, InvoiceResult::setContactName);
        NameEnricher.apply(items, InvoiceResult::getQuotationId, names::quotationCodes, InvoiceResult::setQuotationCode);
        NameEnricher.apply(items, InvoiceResult::getOrderId, names::orderCodes, InvoiceResult::setOrderCode);
        NameEnricher.apply(items, InvoiceResult::getOpportunityId, names::opportunities, InvoiceResult::setOpportunityName);
        NameEnricher.apply(items, InvoiceResult::getCampaignId, names::campaigns, InvoiceResult::setCampaignName);
        NameEnricher.apply(items, InvoiceResult::getOwnerId, names::users, InvoiceResult::setOwnerName);
        NameEnricher.apply(items, InvoiceResult::getCreatedBy, names::users, InvoiceResult::setCreatedByName);
        NameEnricher.apply(items, InvoiceResult::getUpdatedBy, names::users, InvoiceResult::setUpdatedByName);
        return PageResult.<InvoiceResult>builder()
                .items(items)
                .total(page.getTotal()).page(page.getPage()).size(page.getSize()).build();
    }
}
