package vn.com.be_crm.core.dto.handover;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * HTTP request body cho endpoint bàn giao toàn bộ công việc.
 */
@Getter
@NoArgsConstructor
public class HandoverAllRequest {

    /** ID người bàn giao. */
    @NotNull
    private Long fromUserId;

    /** ID người nhận bàn giao. */
    @NotNull
    private Long toUserId;

    /** Lý do bàn giao (tuỳ chọn). */
    private String reason;
}
