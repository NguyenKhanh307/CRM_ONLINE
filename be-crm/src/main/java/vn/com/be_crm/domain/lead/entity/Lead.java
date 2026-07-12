package vn.com.be_crm.domain.lead.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.lead.enums.LeadStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain entity đại diện cho tiềm năng bán hàng.
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Lead {
    /** ID tiềm năng. */
    private Long id;
    /** Mã tiềm năng. */
    private String code;
    /** Tên tiềm năng (người liên hệ). */
    private String name;
    /** Tên tổ chức. */
    private String companyName;
    /** Loại tiềm năng (cá nhân/doanh nghiệp...). */
    private String leadType;
    /** ID người phụ trách. */
    private Long ownerId;
    /** ID khách hàng (sau khi chuyển đổi). */
    private Long customerId;
    /** ID liên hệ. */
    private Long contactId;
    /** ID cơ hội tạo ra khi chuyển đổi. */
    private Long convertedOpportunityId;
    /** Chức danh. */
    private String title;
    /** Phòng ban. */
    private String department;
    /** Mã số thuế. */
    private String taxCode;
    /** Website. */
    private String website;
    /** Ngành nghề. */
    private String industry;
    /** Nguồn tiềm năng. */
    private String source;
    /** Chiến dịch nguồn (attribution). */
    private Long campaignId;
    /** Trạng thái tiềm năng. */
    private LeadStatus status;
    /** Giá trị ước tính. */
    private BigDecimal estimatedValue;
    /** Điểm tiềm năng (lead scoring) — tích lũy từ web tracking & hoạt động. */
    private Integer score;
    /** Số điện thoại. */
    private String phone;
    /** Email. */
    private String email;
    /** Không gọi điện. */
    private boolean doNotCall;
    /** Không gửi email. */
    private boolean doNotEmail;
    /** Ghi chú. */
    private String note;
    /** ID người tạo bản ghi (BE tự đóng dấu, client không gửi lên). */
    private Long createdBy;
    /** ID người sửa bản ghi gần nhất (BE tự đóng dấu). */
    private Long updatedBy;
    /** Thời điểm tạo. */
    private LocalDateTime createdAt;
    /** Thời điểm cập nhật gần nhất. */
    private LocalDateTime updatedAt;
    /** Thời điểm xóa mềm. */
    private LocalDateTime deletedAt;
    /** ID người dùng đã xóa. */
    private Long deletedBy;
    /** True nếu đã ẩn khỏi thùng rác. */
    private boolean isPurged;
}
