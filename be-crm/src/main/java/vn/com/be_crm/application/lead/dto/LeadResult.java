package vn.com.be_crm.application.lead.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.be_crm.domain.lead.enums.LeadStatus;

import java.time.LocalDateTime;

// output cho Lead
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class LeadResult {
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
    private Long campaignId;
    private LeadStatus status;
    private Integer score;
    private String phone;
    private String email;
    private String note;
    // Audit: BE tự đóng dấu (AuditInterceptor), client không gửi lên.
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Tên khóa ngoại — do BE resolve (INameResolver) để FE hiển thị trực tiếp, không còn "#id".
    private String ownerName;
    private String contactName;
    private String campaignName;
    // Tên người tạo/người sửa — do BE resolve (INameResolver).
    private String createdByName;
    private String updatedByName;
}
