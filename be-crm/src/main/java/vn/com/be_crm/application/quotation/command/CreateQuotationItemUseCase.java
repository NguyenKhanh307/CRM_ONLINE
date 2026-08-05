package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.application.quotation.dto.CreateQuotationItemCommand;
import vn.com.be_crm.application.quotation.dto.QuotationItemResult;
import vn.com.be_crm.application.quotation.mapper.QuotationItemCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.quotation.entity.QuotationItem;
import vn.com.be_crm.domain.quotation.repository.IQuotationItemRepository;

// tạo mới dòng hàng báo giá. Thành tiền không lưu DB — tính lúc đọc (QuotationItemCommandMapper.toResult)
public class CreateQuotationItemUseCase implements IUseCase<CreateQuotationItemCommand, QuotationItemResult> {
    private final IQuotationItemRepository repo;

    public CreateQuotationItemUseCase(IQuotationItemRepository repo) {
        this.repo = repo;
    }

    @Override public QuotationItemResult execute(CreateQuotationItemCommand cmd) {
        QuotationItem saved = repo.save(QuotationItemCommandMapper.toEntity(cmd));
        return QuotationItemCommandMapper.toResult(saved);
    }
}
