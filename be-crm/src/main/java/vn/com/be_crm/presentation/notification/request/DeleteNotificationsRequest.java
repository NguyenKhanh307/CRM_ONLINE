package vn.com.be_crm.presentation.notification.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * HTTP request body cho endpoint xóa mềm nhiều thông báo được chọn.
 */
@Getter
@NoArgsConstructor
public class DeleteNotificationsRequest {

    /** Danh sách ID thông báo cần xóa. */
    @NotEmpty
    private List<Long> ids;
}
