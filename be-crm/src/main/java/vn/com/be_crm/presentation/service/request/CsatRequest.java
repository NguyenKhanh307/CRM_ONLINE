package vn.com.be_crm.presentation.service.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Body cho hành động đánh giá hài lòng (CSAT). */
@Getter @Setter @NoArgsConstructor
public class CsatRequest {
    /** Điểm hài lòng 1-5. */
    private Integer score;
    /** Nhận xét. */
    private String comment;
}
