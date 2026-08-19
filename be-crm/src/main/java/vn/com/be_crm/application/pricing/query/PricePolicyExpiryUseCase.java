package vn.com.be_crm.application.pricing.query;

import vn.com.be_crm.application.notification.command.CreateNotificationUseCase;
import vn.com.be_crm.domain.pricing.entity.PricePolicy;
import vn.com.be_crm.domain.pricing.enums.PricePolicyStatus;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyRepository;

import java.time.LocalDate;
import java.util.List;

// Tự đổi chính sách giá quá hạn hiệu lực (endDate đã qua) sang status='expired' + tạo thông báo cho
// người tạo (price_policies không có owner_id) — chạy NGAY LÚC ĐỌC, cùng cơ chế với
// QuotationExpiryUseCase (xem ghi chú ở đó). targetType để null vì chính sách giá chưa có trang
// deep-link riêng trong MODULE_ROUTES ở FE (giống pattern "handover_all" — chỉ mark-read).
public class PricePolicyExpiryUseCase {
    private final IPricePolicyRepository repo;
    private final CreateNotificationUseCase notifyUC;

    public PricePolicyExpiryUseCase(IPricePolicyRepository repo, CreateNotificationUseCase notifyUC) {
        this.repo = repo;
        this.notifyUC = notifyUC;
    }

    // trả về chính sách giá đã cập nhật nếu vừa phát hiện quá hạn (status=expired), ngược lại trả nguyên bản
    public PricePolicy checkAndExpire(PricePolicy p) {
        if (p.getStatus() != PricePolicyStatus.active || p.getEndDate() == null
                || !p.getEndDate().isBefore(LocalDate.now())) {
            return p;
        }
        PricePolicy updated = repo.save(p.toBuilder().status(PricePolicyStatus.expired).build());
        if (updated.getCreatedBy() != null) {
            notifyUC.execute(List.of(updated.getCreatedBy()), "pricing_expired",
                    "Chính sách giá hết hạn: " + updated.getCode(),
                    "Chính sách giá " + updated.getCode() + " đã hết hạn hiệu lực từ ngày " + updated.getEndDate() + ".",
                    null, null);
        }
        return updated;
    }
}
