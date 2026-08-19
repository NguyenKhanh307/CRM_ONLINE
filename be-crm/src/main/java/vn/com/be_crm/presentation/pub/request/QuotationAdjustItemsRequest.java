package vn.com.be_crm.presentation.pub.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Body cho đề xuất chỉnh sửa dòng hàng báo giá (khách "Chỉnh sửa" từ trang công khai) — danh sách
 * dòng hàng khách muốn GIỮ LẠI kèm số lượng mới; dòng khách xóa thì không có mặt. Đây chỉ là đề
 * xuất, không áp thẳng vào quotation_items (xem ProposeQuotationAdjustmentUseCase).
 */
@Getter
@Setter
@NoArgsConstructor
public class QuotationAdjustItemsRequest {
    /** Danh sách dòng hàng muốn giữ lại kèm số lượng mới. */
    private List<Item> items;
    /** Ghi chú tùy chọn của khách. */
    private String note;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Item {
        /** ID dòng hàng (quotation_items.id) — phải thuộc đúng báo giá đang phản hồi. */
        private Long id;
        /** Số lượng mới khách đề nghị. */
        private BigDecimal quantity;
    }
}
