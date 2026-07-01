package vn.com.be_crm.presentation.service.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Body cho hành động từ chối trả/đổi — lý do (tùy chọn). */
@Getter @Setter @NoArgsConstructor
public class RejectTicketRequest {
    /** Lý do từ chối. */
    private String reason;
}
