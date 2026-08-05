package vn.com.be_crm.domain.quotation.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.quotation.enums.QuotationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

// báo giá. Không còn lưu subtotal/discount/tax/total — tính tại thời điểm đọc từ dòng hàng
// (xem LineItemTotals + Get/ListQuotationUseCase). Link công khai cho khách phản hồi dùng thẳng
// "code" (không bí mật) thay cho responseToken ngẫu nhiên trước đây.
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Quotation {
    private Long id;
    private String code;
    private Long customerId;
    private Long contactId;
    // cơ hội liên quan (truy vết nguồn)
    private Long opportunityId;
    // ID chính sách giá áp dụng (kế thừa từ cơ hội)
    private Long pricePolicyId;
    // báo giá đồng bộ (primary) với cơ hội — chỉ một báo giá primary/cơ hội
    private boolean isPrimary;
    // khóa dữ liệu (read-only) sau khi đã chuyển thành đơn hàng
    private boolean isLocked;
    private Long ownerId;
    private LocalDate quoteDate;
    private LocalDate validUntil;
    private QuotationStatus status;
    private String note;
    // phản hồi của khách: accepted | adjust | rejected
    private String customerResponse;
    // nội dung điều chỉnh / lý do khách nhập khi phản hồi
    private String customerResponseNote;
    // createdBy/updatedBy do BE tự đóng dấu (audit), client không gửi lên
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long deletedBy;
    private boolean isPurged;
}
