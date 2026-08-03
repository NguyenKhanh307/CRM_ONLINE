package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.application.quotation.dto.QuotationApprovalResult;
import vn.com.be_crm.application.quotation.dto.UpdateQuotationApprovalCommand;
import vn.com.be_crm.application.quotation.mapper.QuotationApprovalCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.quotation.entity.QuotationApproval;
import vn.com.be_crm.domain.quotation.repository.IQuotationApprovalRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/** Use case cập nhật bước phê duyệt báo giá. */
public class UpdateQuotationApprovalUseCase implements IUseCase<UpdateQuotationApprovalCommand, QuotationApprovalResult> {
    private final IQuotationApprovalRepository repo;
    /** @param repo port lưu trữ */
    public UpdateQuotationApprovalUseCase(IQuotationApprovalRepository repo) { this.repo = repo; }
    /** Cập nhật QuotationApproval. @param cmd @return QuotationApprovalResult @throws NotFoundException */
    @Override public QuotationApprovalResult execute(UpdateQuotationApprovalCommand cmd) {
        QuotationApproval e = repo.findById(cmd.getId()).orElseThrow(() -> new NotFoundException("QuotationApproval not found: " + cmd.getId()));
        return QuotationApprovalCommandMapper.toResult(repo.save(QuotationApprovalCommandMapper.toEntity(cmd, e)));
    }
}
