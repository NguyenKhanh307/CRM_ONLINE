package vn.com.be_crm.presentation.lead.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Body cho hành động đánh mất tiềm năng — chứa lý do tùy chọn.
 */
@Getter
@Setter
@NoArgsConstructor
public class LeadActionRequest {
    /** Lý do thất bại. */
    private String reason;
}
