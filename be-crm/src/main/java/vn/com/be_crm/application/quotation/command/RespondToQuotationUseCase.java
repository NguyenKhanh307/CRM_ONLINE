package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.application.notification.command.CreateNotificationUseCase;
import vn.com.be_crm.application.order.dto.OrderResult;
import vn.com.be_crm.core.tx.port.ITransactionRunner;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.enums.QuotationStatus;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;
import vn.com.be_crm.core.error.frontend.DomainException;
import vn.com.be_crm.core.error.frontend.NotFoundException;

import java.util.List;

// xử lý phản hồi báo giá của khách hàng (qua trang công khai, không cần đăng nhập): accept (đồng
// ý -> tự chuyển accepted, tự sinh Đơn hàng), adjust (yêu cầu điều chỉnh), reject (không đồng ý).
// Mỗi phản hồi đều thông báo cho người phụ trách báo giá. Định danh báo giá công khai dùng thẳng
// "code" (không bí mật) thay cho response_token ngẫu nhiên trước đây.
public class RespondToQuotationUseCase {
    private final IQuotationRepository quotationRepo;
    private final CreateNotificationUseCase createNotificationUC;
    private final ConvertQuotationToOrderUseCase convertToOrderUC;
    private final ITransactionRunner tx;

    public RespondToQuotationUseCase(IQuotationRepository quotationRepo, CreateNotificationUseCase createNotificationUC,
                                      ConvertQuotationToOrderUseCase convertToOrderUC, ITransactionRunner tx) {
        this.quotationRepo = quotationRepo;
        this.createNotificationUC = createNotificationUC;
        this.convertToOrderUC = convertToOrderUC;
        this.tx = tx;
    }

    // đổi trạng thái + (nếu đồng ý) tạo đơn hàng + thông báo chạy trong MỘT transaction
    // code: mã báo giá công khai; action: 'accept' | 'adjust' | 'reject'; note: có thể null
    public void execute(String code, String action, String note) {
        tx.call(() -> {
            executeInTx(code, action, note);
            return null;
        });
    }

    // thân nghiệp vụ phản hồi — luôn chạy bên trong transaction do execute() mở
    private void executeInTx(String code, String action, String note) {
        Quotation q = quotationRepo.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy báo giá"));
        if (q.isLocked()) {
            throw new DomainException("Báo giá đã khóa, không thể phản hồi");
        }

        Quotation.QuotationBuilder builder = q.toBuilder().customerResponseNote(note);

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
                // báo giá đã có đơn hàng (race) — không chặn việc ghi nhận phản hồi
            }
        } else if (willAccept) {
            content += " Bạn có thể chuyển thành đơn hàng.";
        }

        if (saved.getOwnerId() != null) {
            createNotificationUC.execute(List.of(saved.getOwnerId()), "quotation_customer_response", title, content, "quotation", saved.getId());
        }
    }

    // trả "(không có)" nếu chuỗi rỗng
    private String nz(String s) {
        return (s == null || s.isBlank()) ? "(không có)" : s;
    }
}
