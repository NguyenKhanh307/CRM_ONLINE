package vn.com.be_crm.presentation.pub.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Body khách gửi đánh giá hài lòng (CSAT) từ trang public theo mã phiếu.
 */
@Getter
@Setter
@NoArgsConstructor
public class CsatRequest {
    /** Điểm hài lòng 1-5. */
    private Integer score;
    /** Nhận xét (tùy chọn). */
    private String comment;
}
