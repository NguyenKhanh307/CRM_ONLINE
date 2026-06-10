package vn.com.be_crm.presentation.shared;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * HTTP request body cho endpoint bàn giao hàng loạt.
 */
@Getter
@NoArgsConstructor
public class HandoverBulkRequest {

    /** Danh sách ID cần bàn giao. */
    @NotEmpty
    private List<Long> ids;

    /** ID người nhận bàn giao. */
    @NotNull
    private Long toUserId;

    /** Lý do bàn giao (tuỳ chọn). */
    private String reason;
}
