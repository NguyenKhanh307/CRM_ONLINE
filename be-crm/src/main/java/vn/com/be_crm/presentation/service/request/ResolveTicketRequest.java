package vn.com.be_crm.presentation.service.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.be_crm.domain.service.enums.ResolutionType;

/** Body cho hành động giải quyết / hoàn tất / duyệt — hình thức giải quyết + ghi chú (đều tùy chọn). */
@Getter @Setter @NoArgsConstructor
public class ResolveTicketRequest {
    /** Hình thức giải quyết (refund/replacement/repair/store_credit/answered). */
    private ResolutionType resolutionType;
    /** Ghi chú giải quyết. */
    private String note;
}
