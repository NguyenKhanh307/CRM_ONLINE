package vn.com.be_crm.domain.order.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.order.enums.OrderStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

// đơn hàng (chốt bán, sinh từ báo giá, tiền đề của hóa đơn). Không còn customerId/contactId/
// opportunityId/campaignId trực tiếp — chỉ còn quotationId, mọi thông tin khách hàng/cơ hội/
// chiến dịch tra qua chuỗi Order -> Quotation. subtotal/discount/tax/total không còn cột lưu
// sẵn — tính từ dòng hàng tại thời điểm đọc (xem LineItemTotals).
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Long id;
    private String code;
    // báo giá nguồn — mọi liên kết khách hàng/liên hệ/cơ hội/chiến dịch tra qua đây
    private Long quotationId;
    private Long ownerId;
    private LocalDate orderDate;
    private LocalDate deliveryDate;
    private OrderStatus status;
    // khóa dữ liệu khi đã xuất hóa đơn (read-only)
    private boolean isLocked;
    private String note;
    // createdBy/updatedBy do BE tự đóng dấu (audit), client không gửi lên
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long deletedBy;
    private boolean isPurged;
}
