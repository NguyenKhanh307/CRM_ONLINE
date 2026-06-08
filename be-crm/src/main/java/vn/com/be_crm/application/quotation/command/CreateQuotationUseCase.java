package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.application.quotation.dto.CreateQuotationCommand;
import vn.com.be_crm.application.quotation.dto.QuotationResult;
import vn.com.be_crm.application.quotation.mapper.QuotationCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;

/** Use case tạo mới báo giá. */
public class CreateQuotationUseCase implements IUseCase<CreateQuotationCommand, QuotationResult> {
    private final IQuotationRepository repo;
    /** @param repo port lưu trữ */
    public CreateQuotationUseCase(IQuotationRepository repo) { this.repo = repo; }
    /** Tạo mới Quotation. @param cmd @return QuotationResult */
    @Override public QuotationResult execute(CreateQuotationCommand cmd) {
        return QuotationCommandMapper.toResult(repo.save(QuotationCommandMapper.toEntity(cmd)));
    }
}
