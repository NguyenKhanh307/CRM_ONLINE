package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.lead.dto.LeadResult;
import vn.com.be_crm.application.lead.dto.RequestProductQuoteCommand;
import vn.com.be_crm.application.notification.command.CreateNotificationUseCase;
import vn.com.be_crm.core.lookup.port.INameResolver;
import vn.com.be_crm.core.notify.port.IManagerResolver;
import vn.com.be_crm.core.tx.port.ITransactionRunner;
import vn.com.be_crm.domain.lead.entity.Lead;
import vn.com.be_crm.domain.lead.entity.LeadItem;
import vn.com.be_crm.domain.lead.entity.LeadTrackingEvent;
import vn.com.be_crm.domain.lead.enums.LeadItemInterestType;
import vn.com.be_crm.domain.lead.repository.ILeadItemRepository;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;
import vn.com.be_crm.domain.lead.repository.ILeadTrackingEventRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// xử lý "Yêu cầu báo giá" trên landing page công khai: cập nhật thông tin liên hệ (nếu có),
// ghi các dòng sản phẩm đã chọn vào lead_items (interestType=requested_quote), ghi lịch sử +
// cộng điểm — cùng khuôn với SubmitTrackingFormUseCase. Ngoài ra LUÔN báo cho người phụ trách +
// quản lý trực tiếp kèm danh sách mặt hàng được yêu cầu báo giá (độc lập với thông báo "đủ điều
// kiện" của AddLeadScoreUseCase, thông báo đó chỉ bắn khi điểm vượt ngưỡng)
public class RequestProductQuoteUseCase {
    private static final int REQUEST_QUOTE_POINTS = 30;

    private final ILeadRepository leadRepo;
    private final ILeadTrackingEventRepository eventRepo;
    private final ILeadItemRepository leadItemRepo;
    private final AddLeadScoreUseCase addScoreUC;
    private final INameResolver names;
    private final IManagerResolver managerResolver;
    private final CreateNotificationUseCase createNotificationUC;
    private final ITransactionRunner tx;

    public RequestProductQuoteUseCase(ILeadRepository leadRepo, ILeadTrackingEventRepository eventRepo,
                                      ILeadItemRepository leadItemRepo, AddLeadScoreUseCase addScoreUC,
                                      INameResolver names, IManagerResolver managerResolver,
                                      CreateNotificationUseCase createNotificationUC, ITransactionRunner tx) {
        this.leadRepo = leadRepo; this.eventRepo = eventRepo; this.leadItemRepo = leadItemRepo;
        this.addScoreUC = addScoreUC; this.names = names; this.managerResolver = managerResolver;
        this.createNotificationUC = createNotificationUC; this.tx = tx;
    }

    // trả null nếu không tìm thấy tiềm năng theo mã (chưa gọi /visit trước đó)
    public LeadResult execute(RequestProductQuoteCommand cmd) {
        return tx.call(() -> {
            Lead lead = leadRepo.findByCode(cmd.getCode()).orElse(null);
            if (lead == null) return null;

            Lead updated = lead.toBuilder()
                    .name(isBlank(cmd.getName()) ? lead.getName() : cmd.getName())
                    .companyName(isBlank(cmd.getCompanyName()) ? lead.getCompanyName() : cmd.getCompanyName())
                    .email(isBlank(cmd.getEmail()) ? lead.getEmail() : cmd.getEmail())
                    .phone(isBlank(cmd.getPhone()) ? lead.getPhone() : cmd.getPhone())
                    .note(isBlank(cmd.getNote()) ? lead.getNote() : cmd.getNote())
                    .build();
            leadRepo.save(updated);

            int itemCount = cmd.getItems() != null ? cmd.getItems().size() : 0;
            if (cmd.getItems() != null) {
                cmd.getItems().forEach(item -> leadItemRepo.save(LeadItem.builder()
                        .leadId(lead.getId()).productId(item.getProductId())
                        .quantity(item.getQuantity() != null ? item.getQuantity() : BigDecimal.ONE)
                        .interestType(LeadItemInterestType.requested_quote).build()));
            }

            eventRepo.save(LeadTrackingEvent.builder()
                    .leadId(lead.getId()).action("request_quote")
                    .label("Yêu cầu báo giá " + itemCount + " sản phẩm").points(REQUEST_QUOTE_POINTS).build());

            if (itemCount > 0) {
                notifyQuoteRequested(updated, cmd);
            }

            return addScoreUC.execute(lead.getId(), REQUEST_QUOTE_POINTS);
        });
    }

    // báo cho người phụ trách + quản lý trực tiếp, kèm tên các mặt hàng được yêu cầu báo giá
    private void notifyQuoteRequested(Lead lead, RequestProductQuoteCommand cmd) {
        List<Long> productIds = cmd.getItems().stream()
                .map(RequestProductQuoteCommand.Item::getProductId).collect(Collectors.toList());
        Map<Long, String> productNames = names.products(productIds);
        String items = cmd.getItems().stream()
                .map(item -> productNames.getOrDefault(item.getProductId(), "#" + item.getProductId())
                        + " x" + (item.getQuantity() != null ? item.getQuantity() : BigDecimal.ONE))
                .collect(Collectors.joining(", "));

        List<Long> recipients = new ArrayList<>();
        if (lead.getOwnerId() != null) {
            recipients.add(lead.getOwnerId());
            recipients.addAll(managerResolver.managersOf(lead.getOwnerId()));
        } else {
            recipients.addAll(managerResolver.managersOf(null));
        }

        String title = "Yêu cầu báo giá: " + lead.getCode();
        String content = "Tiềm năng " + lead.getName() + " (" + lead.getCode() + ") vừa yêu cầu báo giá: " + items;
        createNotificationUC.execute(recipients, "lead_quote_requested", title, content, "lead", lead.getId());
    }

    private boolean isBlank(String s) { return s == null || s.isBlank(); }
}
