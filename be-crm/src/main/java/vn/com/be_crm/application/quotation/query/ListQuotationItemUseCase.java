package vn.com.be_crm.application.quotation.query;

import vn.com.be_crm.application.quotation.dto.QuotationItemResult;
import vn.com.be_crm.application.quotation.mapper.QuotationItemCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.quotation.repository.IQuotationItemRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy danh sách dòng báo giá theo quotationId. */
public class ListQuotationItemUseCase implements IUseCase<Long, List<QuotationItemResult>> {
    private final IQuotationItemRepository repo;
    /** @param repo port lưu trữ */
    public ListQuotationItemUseCase(IQuotationItemRepository repo) { this.repo = repo; }
    /** Lấy danh sách QuotationItem theo quotationId. @param quotationId @return danh sách */
    @Override public List<QuotationItemResult> execute(Long quotationId) {
        return repo.findAllByQuotationId(quotationId).stream()
                .map(QuotationItemCommandMapper::toResult).collect(Collectors.toList());
    }
}
