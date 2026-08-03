package vn.com.be_crm.application.quotation.query;

import vn.com.be_crm.application.quotation.dto.QuotationApprovalResult;
import vn.com.be_crm.application.quotation.mapper.QuotationApprovalCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.quotation.repository.IQuotationApprovalRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy danh sách bước phê duyệt báo giá theo quotationId. */
public class ListQuotationApprovalUseCase implements IUseCase<Long, List<QuotationApprovalResult>> {
    private final IQuotationApprovalRepository repo;
    /** @param repo port lưu trữ */
    public ListQuotationApprovalUseCase(IQuotationApprovalRepository repo) { this.repo = repo; }
    /** Lấy danh sách QuotationApproval theo quotationId. @param quotationId @return danh sách */
    @Override public List<QuotationApprovalResult> execute(Long quotationId) {
        return repo.findAllByQuotationId(quotationId).stream()
                .map(QuotationApprovalCommandMapper::toResult).collect(Collectors.toList());
    }
}
