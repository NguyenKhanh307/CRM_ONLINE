package vn.com.be_crm.core.dto.handover;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Command bàn giao hàng loạt bản ghi sang người dùng khác.
 */
@Getter
@Builder
public class HandoverBulkCommand {

    /** Danh sách ID cần bàn giao. */
    private final List<Long> ids;

    /** ID người nhận bàn giao. */
    private final Long toUserId;

    /** ID người thực hiện bàn giao (từ JWT). */
    private final Long currentUserId;

    /** true nếu người thực hiện là ADMIN hoặc SALES_MANAGER. */
    private final boolean adminOrManager;

    /** Lý do bàn giao (tuỳ chọn). */
    private final String reason;
}
