package vn.com.be_crm.application.invoice.query;

import vn.com.be_crm.application.invoice.dto.InvoiceResult;
import vn.com.be_crm.application.invoice.mapper.InvoiceCommandMapper;
import vn.com.be_crm.core.lookup.port.INameResolver;
import vn.com.be_crm.core.lookup.NameEnricher;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

import java.util.List;

/** Use case lấy hóa đơn theo ID — kèm tên khóa ngoại để trang chi tiết hiển thị trực tiếp. */
public class GetInvoiceUseCase implements IUseCase<Long, InvoiceResult> {
    private final IInvoiceRepository repo;
    private final INameResolver names;

    /** @param repo port lưu trữ @param names port tra tên khóa ngoại */
    public GetInvoiceUseCase(IInvoiceRepository repo, INameResolver names) {
        this.repo = repo;
        this.names = names;
    }

    /** Lấy Invoice theo ID. @param id @return InvoiceResult (đã điền tên khóa ngoại) @throws NotFoundException */
    @Override public InvoiceResult execute(Long id) {
        InvoiceResult result = InvoiceCommandMapper.toResult(
                repo.findById(id).orElseThrow(() -> new NotFoundException("Invoice not found: " + id)));
        List<InvoiceResult> one = List.of(result);
        NameEnricher.apply(one, InvoiceResult::getCustomerId, names::customers, InvoiceResult::setCustomerName);
        NameEnricher.apply(one, InvoiceResult::getContactId, names::contacts, InvoiceResult::setContactName);
        NameEnricher.apply(one, InvoiceResult::getQuotationId, names::quotationCodes, InvoiceResult::setQuotationCode);
        NameEnricher.apply(one, InvoiceResult::getOrderId, names::orderCodes, InvoiceResult::setOrderCode);
        NameEnricher.apply(one, InvoiceResult::getOpportunityId, names::opportunities, InvoiceResult::setOpportunityName);
        NameEnricher.apply(one, InvoiceResult::getCampaignId, names::campaigns, InvoiceResult::setCampaignName);
        NameEnricher.apply(one, InvoiceResult::getOwnerId, names::users, InvoiceResult::setOwnerName);
        NameEnricher.apply(one, InvoiceResult::getCreatedBy, names::users, InvoiceResult::setCreatedByName);
        NameEnricher.apply(one, InvoiceResult::getUpdatedBy, names::users, InvoiceResult::setUpdatedByName);
        return result;
    }
}
