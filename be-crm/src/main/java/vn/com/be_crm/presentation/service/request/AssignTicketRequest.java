package vn.com.be_crm.presentation.service.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Body cho hành động giao phiếu — người nhận xử lý. */
@Getter @Setter @NoArgsConstructor
public class AssignTicketRequest {
    /** ID nhân viên nhận xử lý. */
    @NotNull private Long toUserId;
}
