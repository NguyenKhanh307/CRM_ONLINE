package vn.com.be_crm.domain.lead.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.lead.enums.LeadStatus;

import java.time.LocalDateTime;

// tiềm năng bán hàng — chưa qua sàng lọc. Không còn convert tự động: Khách hàng/Liên hệ/Cơ hội
// giờ được tạo riêng bằng tay, contactId/convertedOpportunityId chỉ là liên kết tùy chọn gán tay.
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Lead {
    private Long id;
    private String code;
    private String name;
    private String companyName;
    private String leadType;
    private Long ownerId;
    private Long contactId;
    private Long convertedOpportunityId;
    private String taxCode;
    private String website;
    private String industry;
    private String source;
    // chiến dịch nguồn — dùng để tính ROI
    private Long campaignId;
    private LeadStatus status;
    // điểm tiềm năng, tích lũy từ web tracking + hoạt động — vượt ngưỡng chỉ báo (không tự đổi trạng thái)
    private Integer score;
    private String phone;
    private String email;
    private String note;
    // createdBy/updatedBy do BE tự đóng dấu (audit), client không gửi lên
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long deletedBy;
    // true = đã ẩn khỏi thùng rác (purge), DB vẫn giữ bản ghi soft-delete
    private boolean isPurged;
}
