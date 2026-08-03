package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.core.util.CrossFieldRules;
import vn.com.be_crm.application.quotation.dto.QuotationResult;
import vn.com.be_crm.application.quotation.dto.UpdateQuotationCommand;
import vn.com.be_crm.application.quotation.mapper.QuotationCommandMapper;
import vn.com.be_crm.core.notify.NotifyAssignmentUseCase;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;
import vn.com.be_crm.core.error.frontend.DomainException;
import vn.com.be_crm.core.error.frontend.NotFoundException;

import java.util.Objects;

/** Use case cập nhật báo giá. */
public class UpdateQuotationUseCase implements IUseCase<UpdateQuotationCommand, QuotationResult> {
    private final IQuotationRepository repo;
    private final RecomputeQuotationTotalsUseCase recomputeUC;
    private final NotifyAssignmentUseCase notifyUC;

    /** @param repo port lưu trữ @param recomputeUC tính lại tổng tiền từ dòng hàng @param notifyUC báo cho người phụ trách mới */
    public UpdateQuotationUseCase(IQuotationRepository repo, RecomputeQuotationTotalsUseCase recomputeUC,
                                  NotifyAssignmentUseCase notifyUC) {
        this.repo = repo;
        this.recomputeUC = recomputeUC;
        this.notifyUC = notifyUC;
    }

    /**
     * Cập nhật Quotation. Tổng tiền KHÔNG lấy từ client mà tính lại từ dòng hàng sau khi lưu.
     * @param cmd @return QuotationResult @throws NotFoundException
     */
    @Override public QuotationResult execute(UpdateQuotationCommand cmd) {
        // Ràng buộc khoảng thời gian: ngày hiệu lực không được trước ngày báo giá
        CrossFieldRules.requireDateRange(cmd.getQuoteDate(), cmd.getValidUntil(), "Ngày báo giá", "Ngày hiệu lực");
        Quotation e = repo.findById(cmd.getId()).orElseThrow(() -> new NotFoundException("Quotation not found: " + cmd.getId()));
        // Báo giá đã khóa (đã chuyển thành hóa đơn) là read-only — dấu vết kiểm toán
        if (e.isLocked()) throw new DomainException("Báo giá đã khóa (đã chuyển thành hóa đơn), không thể chỉnh sửa");
        repo.save(QuotationCommandMapper.toEntity(cmd, e));
        recomputeUC.execute(cmd.getId());
        Quotation saved = repo.findById(cmd.getId())
                .orElseThrow(() -> new NotFoundException("Quotation not found: " + cmd.getId()));
        // Đổi người phụ trách → báo cho người nhận việc
        if (!Objects.equals(e.getOwnerId(), saved.getOwnerId())) {
            notifyUC.notifyAssigned("quotation", "báo giá", saved.getOwnerId(), saved.getId(), saved.getCode());
        }
        return QuotationCommandMapper.toResult(saved);
    }
}
