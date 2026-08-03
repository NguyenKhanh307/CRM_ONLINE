package vn.com.be_crm.domain.lead.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.lead.enums.LeadStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// tiềm năng bán hàng — chưa qua sàng lọc, sau convert sẽ tách thành Khách hàng + Liên hệ + Cơ hội
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
    // các ID sau chỉ có giá trị sau khi convert
    private Long customerId;
    private Long contactId;
    private Long convertedOpportunityId;
    private String title;
    private String department;
    private String taxCode;
    private String website;
    private String industry;
    private String source;
    // chiến dịch nguồn — chảy tiếp xuống Opportunity/Order/Invoice khi convert để tính ROI
    private Long campaignId;
    private LeadStatus status;
    private BigDecimal estimatedValue;
    // điểm tiềm năng, tích lũy từ web tracking + hoạt động — vượt ngưỡng tự chuyển qualified
    private Integer score;
    private String phone;
    private String email;
    private boolean doNotCall;
    private boolean doNotEmail;
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
