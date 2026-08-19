package vn.com.be_crm.application.quotation.query;

import vn.com.be_crm.application.notification.command.CreateNotificationUseCase;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.enums.QuotationStatus;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

// Tự đổi báo giá quá hạn hiệu lực (validUntil đã qua) sang status='expired' + tạo thông báo cho
// owner — chạy NGAY LÚC ĐỌC (Get/ListQuotationUseCase gọi checkAndExpire trước khi map kết quả),
// KHÔNG dùng @Scheduled (Render free tier tự ngủ nên job nền không thật sự "tự động" — xem
// CLAUDE.md). Đây là ngoại lệ có chủ đích cho nguyên tắc "query use case không ghi DB": lần đọc đầu
// tiên phát hiện quá hạn sẽ ghi thật + báo 1 lần; các lần đọc sau status đã là 'expired' nên không
// còn khớp điều kiện EXPIRABLE, không lặp lại.
public class QuotationExpiryUseCase {
    private static final Set<QuotationStatus> EXPIRABLE = Set.of(
            QuotationStatus.draft, QuotationStatus.pending, QuotationStatus.approved, QuotationStatus.sent);

    private final IQuotationRepository repo;
    private final CreateNotificationUseCase notifyUC;

    public QuotationExpiryUseCase(IQuotationRepository repo, CreateNotificationUseCase notifyUC) {
        this.repo = repo;
        this.notifyUC = notifyUC;
    }

    // trả về báo giá đã cập nhật nếu vừa phát hiện quá hạn (status=expired), ngược lại trả nguyên bản
    public Quotation checkAndExpire(Quotation e) {
        if (e.getValidUntil() == null || !e.getValidUntil().isBefore(LocalDate.now())
                || !EXPIRABLE.contains(e.getStatus())) {
            return e;
        }
        Quotation updated = repo.save(e.toBuilder().status(QuotationStatus.expired).build());
        if (updated.getOwnerId() != null) {
            notifyUC.execute(List.of(updated.getOwnerId()), "quotation_expired",
                    "Báo giá hết hạn: " + updated.getCode(),
                    "Báo giá " + updated.getCode() + " đã hết hạn hiệu lực từ ngày " + updated.getValidUntil() + ".",
                    "quotation", updated.getId());
        }
        return updated;
    }
}
