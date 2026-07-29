package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.application.notification.command.CreateNotificationUseCase;
import vn.com.be_crm.application.order.dto.OrderResult;
import vn.com.be_crm.application.shared.tx.ITransactionRunner;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.enums.QuotationStatus;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;
import vn.com.be_crm.domain.shared.exception.DomainException;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Use case xử lý phản hồi báo giá của khách hàng (qua trang công khai, không cần đăng nhập):
 * accept (đồng ý → tự chuyển accepted, tự sinh Đơn hàng), adjust (yêu cầu điều chỉnh), reject (không đồng ý).
 * Mỗi phản hồi đều thông báo cho người phụ trách báo giá.
 */
public class RespondToQuotationUseCase {
    private final IQuotationRepository quotationRepo;
    private final CreateNotificationUseCase createNotificationUC;
    private final ConvertQuotationToOrderUseCase convertToOrderUC;
    private final ITransactionRunner tx;

    /** @param quotationRepo báo giá @param createNotificationUC tạo thông báo cho người phụ trách
     *  @param convertToOrderUC tự sinh đơn hàng khi khách chấp nhận @param tx bộ chạy transaction */
    public RespondToQuotationUseCase(IQuotationRepository quotationRepo, CreateNotificationUseCase createNotificationUC,
                                      ConvertQuotationToOrderUseCase convertToOrderUC, ITransactionRunner tx) {
        this.quotationRepo = quotationRepo;
        this.createNotificationUC = createNotificationUC;
        this.convertToOrderUC = convertToOrderUC;
        this.tx = tx;
    }

    /**
     * Ghi nhận phản hồi của khách theo token.
     * Đổi trạng thái + (nếu đồng ý) tạo đơn hàng + thông báo chạy trong MỘT transaction.
     * @param token  token phản hồi công khai
     * @param action 'accept' | 'adjust' | 'reject'
     * @param note   nội dung điều chỉnh / lý do (có thể null)
     */
    public void execute(String token, String action, String note) {
        tx.call(() -> {
            executeInTx(token, action, note);
            return null;
        });
    }

    /** Thân nghiệp vụ phản hồi — luôn chạy bên trong transaction do {@link #execute} mở. */
    private void executeInTx(String token, String action, String note) {
        Quotation q = quotationRepo.findByResponseToken(token)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy báo giá hoặc liên kết đã hết hạn"));
        if (q.isLocked()) {
            throw new DomainException("Báo giá đã khóa, không thể phản hồi");
        }

        Quotation.QuotationBuilder builder = q.toBuilder()
                .customerResponseNote(note)
                .customerRespondedAt(LocalDateTime.now());

        boolean willAccept = false;
        String title;
        String content;
        switch (action == null ? "" : action) {
            case "accept" -> {
                builder.customerResponse("accepted");
                if (q.getStatus() == QuotationStatus.sent) {
                    q.getStatus().ensureCanTransitionTo(QuotationStatus.accepted);
                    builder.status(QuotationStatus.accepted);
                    willAccept = true;
                }
                title = "Khách đồng ý báo giá: " + q.getCode();
                content = "Khách hàng đã ĐỒNG Ý báo giá " + q.getCode() + ".";
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

        if (willAccept && !saved.isLocked()) {
            try {
                OrderResult order = convertToOrderUC.execute(saved.getId());
                content += " Đơn hàng " + order.getCode() + " đã được tạo tự động — bạn có thể xác nhận và xử lý.";
            } catch (DomainException e) {
                // Báo giá đã có đơn hàng (race) — không chặn việc ghi nhận phản hồi.
            }
        } else if (willAccept) {
            content += " Bạn có thể chuyển thành đơn hàng.";
        }

        if (saved.getOwnerId() != null) {
            createNotificationUC.execute(List.of(saved.getOwnerId()), "quotation_customer_response", title, content, null, saved.getId());
        }
    }

    /** Trả "(không có)" nếu chuỗi rỗng. */
    private String nz(String s) {
        return (s == null || s.isBlank()) ? "(không có)" : s;
    }
}
