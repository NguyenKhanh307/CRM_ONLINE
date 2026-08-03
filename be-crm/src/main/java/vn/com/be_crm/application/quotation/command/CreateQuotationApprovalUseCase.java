package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.application.quotation.dto.CreateQuotationApprovalCommand;
import vn.com.be_crm.application.quotation.dto.QuotationApprovalResult;
import vn.com.be_crm.application.quotation.mapper.QuotationApprovalCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.quotation.repository.IQuotationApprovalRepository;

/** Use case tạo mới bước phê duyệt báo giá. */
public class CreateQuotationApprovalUseCase implements IUseCase<CreateQuotationApprovalCommand, QuotationApprovalResult> {
    private final IQuotationApprovalRepository repo;
    /** @param repo port lưu trữ */
    public CreateQuotationApprovalUseCase(IQuotationApprovalRepository repo) { this.repo = repo; }
    /** Tạo mới QuotationApproval. @param cmd @return QuotationApprovalResult */
    @Override public QuotationApprovalResult execute(CreateQuotationApprovalCommand cmd) {
        return QuotationApprovalCommandMapper.toResult(repo.save(QuotationApprovalCommandMapper.toEntity(cmd)));
    }
}
