package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.core.util.CrossFieldRules;
import vn.com.be_crm.application.quotation.dto.QuotationResult;
import vn.com.be_crm.application.quotation.dto.UpdateQuotationCommand;
import vn.com.be_crm.application.quotation.mapper.QuotationCommandMapper;
import vn.com.be_crm.core.notify.NotifyAssignmentUseCase;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.core.util.LineItemTotals;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.entity.QuotationItem;
import vn.com.be_crm.domain.quotation.repository.IQuotationItemRepository;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;
import vn.com.be_crm.core.error.frontend.DomainException;
import vn.com.be_crm.core.error.frontend.NotFoundException;

import java.util.List;
import java.util.Objects;

// cập nhật báo giá
public class UpdateQuotationUseCase implements IUseCase<UpdateQuotationCommand, QuotationResult> {
    private final IQuotationRepository repo;
    private final IQuotationItemRepository itemRepo;
    private final NotifyAssignmentUseCase notifyUC;

    public UpdateQuotationUseCase(IQuotationRepository repo, IQuotationItemRepository itemRepo,
                                  NotifyAssignmentUseCase notifyUC) {
        this.repo = repo;
        this.itemRepo = itemRepo;
        this.notifyUC = notifyUC;
    }

    @Override public QuotationResult execute(UpdateQuotationCommand cmd) {
        // ràng buộc khoảng thời gian: ngày hiệu lực không được trước ngày báo giá
        CrossFieldRules.requireDateRange(cmd.getQuoteDate(), cmd.getValidUntil(), "Ngày báo giá", "Ngày hiệu lực");
        Quotation e = repo.findById(cmd.getId()).orElseThrow(() -> new NotFoundException("Quotation not found: " + cmd.getId()));
        // báo giá đã khóa (đã chuyển thành đơn hàng) là read-only — dấu vết kiểm toán
        if (e.isLocked()) throw new DomainException("Báo giá đã khóa (đã chuyển thành đơn hàng), không thể chỉnh sửa");
        Quotation saved = repo.save(QuotationCommandMapper.toEntity(cmd, e));

        QuotationResult result = QuotationCommandMapper.toResult(saved);
        List<QuotationItem> items = itemRepo.findAllByQuotationId(saved.getId());
        LineItemTotals.Totals t = LineItemTotals.compute(items,
                QuotationItem::getQuantity, QuotationItem::getUnitPrice, QuotationItem::getDiscount, QuotationItem::getTaxRate);
        result.setSubtotal(t.subtotal()); result.setDiscount(t.discount());
        result.setTax(t.tax()); result.setTotal(t.total());

        // đổi người phụ trách -> báo cho người nhận việc
        if (!Objects.equals(e.getOwnerId(), saved.getOwnerId())) {
            notifyUC.notifyAssigned("quotation", "báo giá", saved.getOwnerId(), saved.getId(), saved.getCode());
        }
        return result;
    }
}
