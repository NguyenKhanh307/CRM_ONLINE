package vn.com.be_crm.domain.notification.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// nội dung một thông báo — dùng chung cho mọi người nhận (xem NotificationRecipient cho trạng
// thái đọc/xóa theo từng người). targetType/targetId trỏ tới bản ghi liên quan (lead/quotation/
// ticket...) — dùng cho click-to-focus ở FE, cùng khuôn với activities.target_type/target_id.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private Long id;
    // loại thông báo (vd lead_hot)
    private String type;
    private String title;
    private String content;
    private String targetType;
    private Long targetId;
    private LocalDateTime createdAt;
}
