package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.quotation.entity.QuotationItem;
import vn.com.be_crm.domain.quotation.repository.IQuotationItemRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/** Use case xóa dòng hàng báo giá. Tổng chứng từ được tính lại sau khi xóa. */
public class DeleteQuotationItemUseCase implements IUseCase<Long, Void> {
    private final IQuotationItemRepository repo;
    private final RecomputeQuotationTotalsUseCase recomputeUC;

    /** @param repo port lưu trữ @param recomputeUC tính lại tổng tiền chứng từ */
    public DeleteQuotationItemUseCase(IQuotationItemRepository repo, RecomputeQuotationTotalsUseCase recomputeUC) {
        this.repo = repo;
        this.recomputeUC = recomputeUC;
    }

    /** Xóa QuotationItem rồi tính lại tổng chứng từ. @param id @return null @throws NotFoundException */
    @Override public Void execute(Long id) {
        QuotationItem e = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("QuotationItem not found: " + id));
        Long parentId = e.getQuotationId();
        repo.deleteById(id);
        recomputeUC.execute(parentId);
        return null;
    }
}
