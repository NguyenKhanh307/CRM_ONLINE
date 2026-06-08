package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.application.quotation.dto.CreateQuotationItemCommand;
import vn.com.be_crm.application.quotation.dto.QuotationItemResult;
import vn.com.be_crm.application.quotation.mapper.QuotationItemCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.quotation.repository.IQuotationItemRepository;

/** Use case tạo mới dòng báo giá. */
public class CreateQuotationItemUseCase implements IUseCase<CreateQuotationItemCommand, QuotationItemResult> {
    private final IQuotationItemRepository repo;
    /** @param repo port lưu trữ */
    public CreateQuotationItemUseCase(IQuotationItemRepository repo) { this.repo = repo; }
    /** Tạo mới QuotationItem. @param cmd @return QuotationItemResult */
    @Override public QuotationItemResult execute(CreateQuotationItemCommand cmd) {
        return QuotationItemCommandMapper.toResult(repo.save(QuotationItemCommandMapper.toEntity(cmd)));
    }
}
