package vn.com.be_crm.application.lead.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Input DTO khi cập nhật bản ghi chuyển giao tiềm năng. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateLeadTransferCommand {
    private Long id;
    private Long fromUserId;
    private Long toUserId;
    @Size(max = 500) private String reason;
}
