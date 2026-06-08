package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.application.quotation.dto.QuotationItemResult;
import vn.com.be_crm.application.quotation.dto.UpdateQuotationItemCommand;
import vn.com.be_crm.application.quotation.mapper.QuotationItemCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.quotation.entity.QuotationItem;
import vn.com.be_crm.domain.quotation.repository.IQuotationItemRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case cập nhật dòng báo giá. */
public class UpdateQuotationItemUseCase implements IUseCase<UpdateQuotationItemCommand, QuotationItemResult> {
    private final IQuotationItemRepository repo;
    /** @param repo port lưu trữ */
    public UpdateQuotationItemUseCase(IQuotationItemRepository repo) { this.repo = repo; }
    /** Cập nhật QuotationItem. @param cmd @return QuotationItemResult @throws NotFoundException */
    @Override public QuotationItemResult execute(UpdateQuotationItemCommand cmd) {
        QuotationItem e = repo.findById(cmd.getId()).orElseThrow(() -> new NotFoundException("QuotationItem not found: " + cmd.getId()));
        return QuotationItemCommandMapper.toResult(repo.save(QuotationItemCommandMapper.toEntity(cmd, e)));
    }
}
