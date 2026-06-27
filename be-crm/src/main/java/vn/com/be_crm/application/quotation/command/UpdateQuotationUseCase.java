package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.application.quotation.dto.QuotationResult;
import vn.com.be_crm.application.quotation.dto.UpdateQuotationCommand;
import vn.com.be_crm.application.quotation.mapper.QuotationCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;
import vn.com.be_crm.domain.shared.exception.DomainException;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case cập nhật báo giá. */
public class UpdateQuotationUseCase implements IUseCase<UpdateQuotationCommand, QuotationResult> {
    private final IQuotationRepository repo;
    /** @param repo port lưu trữ */
    public UpdateQuotationUseCase(IQuotationRepository repo) { this.repo = repo; }
    /** Cập nhật Quotation. @param cmd @return QuotationResult @throws NotFoundException */
    @Override public QuotationResult execute(UpdateQuotationCommand cmd) {
        Quotation e = repo.findById(cmd.getId()).orElseThrow(() -> new NotFoundException("Quotation not found: " + cmd.getId()));
        // Báo giá đã khóa (đã chuyển thành hóa đơn) là read-only — dấu vết kiểm toán
        if (e.isLocked()) throw new DomainException("Báo giá đã khóa (đã chuyển thành hóa đơn), không thể chỉnh sửa");
        return QuotationCommandMapper.toResult(repo.save(QuotationCommandMapper.toEntity(cmd, e)));
    }
}
