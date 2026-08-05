package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.application.quotation.dto.UpdateQuotationItemCommand;
import vn.com.be_crm.application.quotation.dto.QuotationItemResult;
import vn.com.be_crm.application.quotation.mapper.QuotationItemCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.quotation.entity.QuotationItem;
import vn.com.be_crm.domain.quotation.repository.IQuotationItemRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

// cập nhật dòng hàng báo giá. Thành tiền không lưu DB — tính lúc đọc (QuotationItemCommandMapper.toResult)
public class UpdateQuotationItemUseCase implements IUseCase<UpdateQuotationItemCommand, QuotationItemResult> {
    private final IQuotationItemRepository repo;

    public UpdateQuotationItemUseCase(IQuotationItemRepository repo) {
        this.repo = repo;
    }

    @Override public QuotationItemResult execute(UpdateQuotationItemCommand cmd) {
        QuotationItem existing = repo.findById(cmd.getId())
                .orElseThrow(() -> new NotFoundException("QuotationItem not found: " + cmd.getId()));
        QuotationItem saved = repo.save(QuotationItemCommandMapper.toEntity(cmd, existing));
        return QuotationItemCommandMapper.toResult(saved);
    }
}
