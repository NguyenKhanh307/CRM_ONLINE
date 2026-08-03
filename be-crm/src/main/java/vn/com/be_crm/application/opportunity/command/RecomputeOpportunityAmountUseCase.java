package vn.com.be_crm.application.opportunity.command;

import vn.com.be_crm.core.util.LineItemTotals;
import vn.com.be_crm.domain.opportunity.entity.Opportunity;
import vn.com.be_crm.domain.opportunity.entity.OpportunityItem;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityItemRepository;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository;

import java.math.BigDecimal;

/**
 * Use case tính lại (roll-up) giá trị cơ hội = tổng thành tiền các dòng hàng.
 * Gọi sau mỗi lần tạo/sửa/xóa dòng hàng cơ hội hoặc khi sync từ báo giá đồng bộ.
 */
public class RecomputeOpportunityAmountUseCase {
    private final IOpportunityRepository opportunityRepo;
    private final IOpportunityItemRepository itemRepo;

    /** @param opportunityRepo cơ hội @param itemRepo dòng hàng cơ hội */
    public RecomputeOpportunityAmountUseCase(IOpportunityRepository opportunityRepo, IOpportunityItemRepository itemRepo) {
        this.opportunityRepo = opportunityRepo;
        this.itemRepo = itemRepo;
    }

    /**
     * Tính lại amount của cơ hội từ tổng dòng hàng và lưu nếu thay đổi.
     * @param opportunityId ID cơ hội
     */
    public void execute(Long opportunityId) {
        Opportunity o = opportunityRepo.findById(opportunityId).orElse(null);
        if (o == null) return;
        BigDecimal total = LineItemTotals.sumAmount(itemRepo.findAllByOpportunityId(opportunityId), OpportunityItem::getAmount);
        if (total.compareTo(o.getAmount() != null ? o.getAmount() : BigDecimal.ZERO) != 0) {
            opportunityRepo.save(o.toBuilder().amount(total).build());
        }
    }
}
