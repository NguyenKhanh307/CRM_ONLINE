package vn.com.be_crm.application.shared.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * Command bàn giao toàn bộ công việc từ user này sang user khác (5 module).
 */
@Getter
@Builder
public class HandoverAllCommand {

    /** ID người bàn giao. */
    private final Long fromUserId;

    /** ID người nhận bàn giao. */
    private final Long toUserId;

    /** Lý do bàn giao (tuỳ chọn). */
    private final String reason;
}
