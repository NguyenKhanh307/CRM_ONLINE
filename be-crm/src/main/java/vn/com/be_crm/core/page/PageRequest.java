package vn.com.be_crm.core.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Tham số phân trang và sắp xếp dùng cho tất cả List UseCase.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest {

    /** Số trang bắt đầu từ 0. */
    @Builder.Default
    private int page = 0;

    /** Số bản ghi mỗi trang. */
    @Builder.Default
    private int size = 20;

    /** Tên cột sắp xếp (tên field Java, không phải tên cột DB). */
    @Builder.Default
    private String sortBy = "id";

    /** Chiều sắp xếp: "asc" hoặc "desc". */
    @Builder.Default
    private String sortDir = "asc";

    /** Năm sớm nhất được xem data — null nghĩa là không giới hạn theo năm. */
    private Integer dataAccessFromYear;

    /** Lọc theo trạng thái (giá trị tag lọc nhanh của module) — null nghĩa là không lọc. */
    private String status;

    /** Từ khóa tìm kiếm server-side (LIKE trên các cột chính của module) — null/rỗng nghĩa là không tìm. */
    private String q;

    /** Chỉ trả bản ghi thuộc owner này (record-level visibility) — null nghĩa là không lọc (admin/manager). */
    private Long ownerId;

    /**
     * Khi có {@code ownerId} lọc, cho phép trả THÊM các bản ghi chưa có người phụ trách
     * (owner_id IS NULL) — dùng cho tiềm năng: nhân viên cần thấy tiềm năng trong "pool" chung
     * để bấm "Nhận chăm sóc". Mặc định false — không ảnh hưởng các module khác.
     */
    @Builder.Default
    private boolean includeUnassigned = false;

    /**
     * Chỉ trả bản ghi thuộc khách hàng này — null nghĩa là không lọc.
     * Hiện dùng cho danh sách liên hệ: ô chọn Liên hệ trong form phải thu hẹp theo khách hàng đang chọn,
     * không thể để người dùng dò trong hàng chục nghìn liên hệ.
     */
    private Long customerId;

    /**
     * Loại bỏ bản ghi đã hết hạn (status='expired') khỏi kết quả — dùng cho ô chọn báo giá ở form
     * khác (Đơn hàng/Hóa đơn), KHÔNG bật ở trang danh sách quản lý chính (vẫn cần thấy bản ghi hết
     * hạn để theo dõi/xử lý). Mặc định false.
     */
    @Builder.Default
    private boolean excludeExpired = false;

    /** @return offset = page * size */
    public int getOffset() {
        return page * size;
    }
}
