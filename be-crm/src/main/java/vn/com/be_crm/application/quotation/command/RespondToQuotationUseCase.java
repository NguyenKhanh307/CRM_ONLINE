package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.application.notification.command.CreateNotificationUseCase;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.enums.QuotationStatus;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;
import vn.com.be_crm.domain.shared.exception.DomainException;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Use case xử lý phản hồi báo giá của khách hàng (qua trang công khai, không cần đăng nhập):
 * accept (đồng ý → tự chuyển accepted), adjust (yêu cầu điều chỉnh), reject (không đồng ý).
 * Mỗi phản hồi đều thông báo cho người phụ trách báo giá.
 */
public class RespondToQuotationUseCase {
    private final IQuotationRepository quotationRepo;
    private final CreateNotificationUseCase createNotificationUC;

    /** @param quotationRepo báo giá @param createNotificationUC tạo thông báo cho người phụ trách */
    public RespondToQuotationUseCase(IQuotationRepository quotationRepo, CreateNotificationUseCase createNotificationUC) {
        this.quotationRepo = quotationRepo;
        this.createNotificationUC = createNotificationUC;
    }

    /**
     * Ghi nhận phản hồi của khách theo token.
     * @param token  token phản hồi công khai
     * @param action 'accept' | 'adjust' | 'reject'
     * @param note   nội dung điều chỉnh / lý do (có thể null)
     */
    public void execute(String token, String action, String note) {
        Quotation q = quotationRepo.findByResponseToken(token)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy báo giá hoặc liên kết đã hết hạn"));
        if (q.isLocked()) {
            throw new DomainException("Báo giá đã khóa, không thể phản hồi");
        }

        Quotation.QuotationBuilder builder = q.toBuilder()
                .customerResponseNote(note)
                .customerRespondedAt(LocalDateTime.now());

        String title;
        String content;
        switch (action == null ? "" : action) {
            case "accept" -> {
                builder.customerResponse("accepted");
                if (q.getStatus() == QuotationStatus.sent) {
                    q.getStatus().ensureCanTransitionTo(QuotationStatus.accepted);
                    builder.status(QuotationStatus.accepted);
                }
                title = "Khách đồng ý báo giá: " + q.getCode();
                content = "Khách hàng đã ĐỒNG Ý báo giá " + q.getCode() + ". Bạn có thể chuyển thành hóa đơn.";
            }
            case "adjust" -> {
                builder.customerResponse("adjust");
                title = "Khách yêu cầu điều chỉnh báo giá: " + q.getCode();
                content = "Khách yêu cầu ĐIỀU CHỈNH báo giá " + q.getCode() + ". Nội dung: " + nz(note);
            }
            case "reject" -> {
                builder.customerResponse("rejected");
                title = "Khách không đồng ý báo giá: " + q.getCode();
                content = "Khách KHÔNG ĐỒNG Ý báo giá " + q.getCode() + ". Lý do: " + nz(note);
            }
            default -> throw new DomainException("Hành động phản hồi không hợp lệ: " + action);
        }

        Quotation saved = quotationRepo.save(builder.build());
        if (saved.getOwnerId() != null) {
            createNotificationUC.execute(List.of(saved.getOwnerId()), "quotation_customer_response", title, content, null, saved.getId());
        }
    }

    /** Trả "(không có)" nếu chuỗi rỗng. */
    private String nz(String s) {
        return (s == null || s.isBlank()) ? "(không có)" : s;
    }
}
