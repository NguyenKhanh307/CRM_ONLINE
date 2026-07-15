package vn.com.be_crm.application.quotation.query;

import vn.com.be_crm.application.quotation.dto.QuotationResult;
import vn.com.be_crm.application.quotation.mapper.QuotationCommandMapper;
import vn.com.be_crm.application.shared.lookup.INameResolver;
import vn.com.be_crm.application.shared.lookup.NameEnricher;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

import java.util.List;

/** Use case lấy báo giá theo ID — kèm tên khóa ngoại để trang chi tiết hiển thị trực tiếp. */
public class GetQuotationUseCase implements IUseCase<Long, QuotationResult> {
    private final IQuotationRepository repo;
    private final INameResolver names;

    /** @param repo port lưu trữ @param names port tra tên khóa ngoại */
    public GetQuotationUseCase(IQuotationRepository repo, INameResolver names) {
        this.repo = repo;
        this.names = names;
    }

    /** Lấy Quotation theo ID. @param id @return QuotationResult (đã điền tên khóa ngoại) @throws NotFoundException */
    @Override public QuotationResult execute(Long id) {
        QuotationResult result = QuotationCommandMapper.toResult(
                repo.findById(id).orElseThrow(() -> new NotFoundException("Quotation not found: " + id)));
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
