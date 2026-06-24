package vn.com.be_crm.domain.notification.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Domain entity đại diện cho một thông báo gửi tới người dùng.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    /** ID thông báo. */
    private Long id;
    /** ID người nhận. */
    private Long recipientUserId;
    /** Loại thông báo (vd lead_hot). */
    private String type;
    /** Tiêu đề. */
    private String title;
    /** Nội dung. */
    private String content;
    /** ID tiềm năng liên quan (nếu có). */
    private Long leadId;
    /** Đã đọc chưa. */
    private boolean isRead;
    /** Thời điểm tạo. */
    private LocalDateTime createdAt;
}
