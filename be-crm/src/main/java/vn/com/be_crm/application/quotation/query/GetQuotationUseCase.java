package vn.com.be_crm.application.quotation.query;

import vn.com.be_crm.application.quotation.dto.QuotationResult;
import vn.com.be_crm.application.quotation.mapper.QuotationCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case lấy báo giá theo ID. */
public class GetQuotationUseCase implements IUseCase<Long, QuotationResult> {
    private final IQuotationRepository repo;
    /** @param repo port lưu trữ */
    public GetQuotationUseCase(IQuotationRepository repo) { this.repo = repo; }
    /** Lấy Quotation theo ID. @param id @return QuotationResult @throws NotFoundException */
    @Override public QuotationResult execute(Long id) {
        return QuotationCommandMapper.toResult(repo.findById(id).orElseThrow(() -> new NotFoundException("Quotation not found: " + id)));
    }
}
