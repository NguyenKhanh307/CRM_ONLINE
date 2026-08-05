package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.lead.dto.LeadResult;
import vn.com.be_crm.application.lead.dto.TrackProductViewCommand;
import vn.com.be_crm.application.lead.mapper.LeadCommandMapper;
import vn.com.be_crm.core.tx.port.ITransactionRunner;
import vn.com.be_crm.domain.lead.entity.Lead;
import vn.com.be_crm.domain.lead.entity.LeadItem;
import vn.com.be_crm.domain.lead.entity.LeadTrackingEvent;
import vn.com.be_crm.domain.lead.enums.LeadItemInterestType;
import vn.com.be_crm.domain.lead.repository.ILeadItemRepository;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;
import vn.com.be_crm.domain.lead.repository.ILeadTrackingEventRepository;
import vn.com.be_crm.domain.product.entity.Product;
import vn.com.be_crm.domain.product.repository.IProductRepository;

import java.math.BigDecimal;

// xử lý "xem chi tiết sản phẩm" trên landing page công khai — ghi nhận sản phẩm quan tâm vào
// lead_items (interestType=viewed) + cộng điểm nhẹ; chỉ ghi ở lượt xem đầu tiên của mỗi sản
// phẩm, xem lại không tạo thêm bản ghi hay cộng điểm lần nữa
public class TrackProductViewUseCase {
    private static final int VIEW_POINTS = 2;

    private final ILeadRepository leadRepo;
    private final ILeadItemRepository leadItemRepo;
    private final IProductRepository productRepo;
    private final ILeadTrackingEventRepository eventRepo;
    private final AddLeadScoreUseCase addScoreUC;
    private final ITransactionRunner tx;

    public TrackProductViewUseCase(ILeadRepository leadRepo, ILeadItemRepository leadItemRepo,
                                   IProductRepository productRepo, ILeadTrackingEventRepository eventRepo,
                                   AddLeadScoreUseCase addScoreUC, ITransactionRunner tx) {
        this.leadRepo = leadRepo; this.leadItemRepo = leadItemRepo; this.productRepo = productRepo;
        this.eventRepo = eventRepo; this.addScoreUC = addScoreUC; this.tx = tx;
    }

    // trả null nếu không tìm thấy tiềm năng theo mã hoặc sản phẩm không tồn tại
    public LeadResult execute(TrackProductViewCommand cmd) {
        return tx.call(() -> {
            Lead lead = leadRepo.findByCode(cmd.getCode()).orElse(null);
            if (lead == null) return null;
            Product product = productRepo.findById(cmd.getProductId()).orElse(null);
            if (product == null) return null;

            boolean alreadyViewed = leadItemRepo.findAllByLeadId(lead.getId()).stream()
                    .anyMatch(item -> cmd.getProductId().equals(item.getProductId()));
            if (alreadyViewed) return LeadCommandMapper.toResult(lead);

            leadItemRepo.save(LeadItem.builder()
                    .leadId(lead.getId()).productId(product.getId())
                    .quantity(BigDecimal.ONE).interestType(LeadItemInterestType.viewed).build());

            eventRepo.save(LeadTrackingEvent.builder()
                    .leadId(lead.getId()).action("view_product")
                    .label("Xem sản phẩm: " + product.getName()).points(VIEW_POINTS).build());

            return addScoreUC.execute(lead.getId(), VIEW_POINTS);
        });
    }
}
