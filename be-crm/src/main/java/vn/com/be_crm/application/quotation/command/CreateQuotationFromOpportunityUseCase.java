package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.application.quotation.dto.QuotationResult;
import vn.com.be_crm.application.quotation.mapper.QuotationCommandMapper;
import vn.com.be_crm.application.shared.util.LineItemTotals;
import vn.com.be_crm.domain.opportunity.entity.Opportunity;
import vn.com.be_crm.domain.opportunity.entity.OpportunityItem;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityItemRepository;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.entity.QuotationItem;
import vn.com.be_crm.domain.quotation.enums.QuotationStatus;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Use case clone (sao chép sâu) báo giá từ cơ hội: kế thừa khách hàng/liên hệ/chính sách giá
 * và toàn bộ dòng hàng (OLI → QLI). Báo giá đầu tiên của cơ hội được đánh dấu đồng bộ (primary).
 */
public class CreateQuotationFromOpportunityUseCase {
    private final IQuotationRepository quotationRepo;
    private final IOpportunityRepository opportunityRepo;
    private final IOpportunityItemRepository opportunityItemRepo;

    /** @param quotationRepo báo giá @param opportunityRepo cơ hội @param opportunityItemRepo dòng hàng cơ hội */
    public CreateQuotationFromOpportunityUseCase(IQuotationRepository quotationRepo, IOpportunityRepository opportunityRepo,
                                                 IOpportunityItemRepository opportunityItemRepo) {
        this.quotationRepo = quotationRepo;
        this.opportunityRepo = opportunityRepo;
        this.opportunityItemRepo = opportunityItemRepo;
    }

    /**
     * Tạo báo giá mới từ cơ hội.
     * @param opportunityId ID cơ hội nguồn @return báo giá vừa tạo
     */
    public QuotationResult execute(Long opportunityId) {
        Opportunity opp = opportunityRepo.findById(opportunityId)
                .orElseThrow(() -> new NotFoundException("Opportunity not found: " + opportunityId));

        List<OpportunityItem> oppItems = opportunityItemRepo.findAllByOpportunityId(opportunityId);
        // Map mỗi dòng cơ hội (OLI) → dòng báo giá (QLI), giữ tham chiếu nguồn để sync hai chiều
        List<QuotationItem> qItems = oppItems.stream().map(oi -> QuotationItem.builder()
                .productId(oi.getProductId())
                .opportunityItemId(oi.getId())
                .quantity(oi.getQuantity())
                .unitPrice(oi.getUnitPrice())
                .discount(oi.getDiscount())
                .taxRate(BigDecimal.ZERO)
                .amount(oi.getAmount())
                .build()).collect(Collectors.toList());

        BigDecimal total = LineItemTotals.sumAmount(qItems, QuotationItem::getAmount);
        // Báo giá đầu tiên của cơ hội → tự đánh dấu đồng bộ (primary)
        boolean firstQuote = quotationRepo.findAllByOpportunityId(opportunityId).isEmpty();

        Quotation quote = Quotation.builder()
                .code("BG-" + System.currentTimeMillis())
                .customerId(opp.getCustomerId()).contactId(opp.getContactId())
                .opportunityId(opportunityId).campaignId(opp.getCampaignId())
                .pricePolicyId(opp.getPricePolicyId())
                .ownerId(opp.getOwnerId())
                .isPrimary(firstQuote)
                .status(QuotationStatus.draft)
                .subtotal(total).discount(BigDecimal.ZERO).tax(BigDecimal.ZERO).total(total)
                .build();

        return QuotationCommandMapper.toResult(quotationRepo.saveWithItems(quote, qItems));
    }
}
