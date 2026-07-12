package vn.com.be_crm.domain.campaign.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.campaign.enums.CampaignStatus;
import vn.com.be_crm.domain.campaign.enums.CampaignType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Domain entity đại diện cho chiến dịch marketing (đầu phễu: sinh tiềm năng + đo ROI).
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Campaign {
    /** ID chiến dịch. */
    private Long id;
    /** Mã chiến dịch. */
    private String code;
    /** Tên chiến dịch. */
    private String name;
    /** Loại chiến dịch. */
    private CampaignType type;
    /** Trạng thái chiến dịch. */
    private CampaignStatus status;
    /** Kênh triển khai. */
    private String channel;
    /** Ngày bắt đầu. */
    private LocalDate startDate;
    /** Ngày kết thúc. */
    private LocalDate endDate;
    /** Chi phí dự kiến. */
    private BigDecimal budget;
    /** Chi phí thực tế. */
    private BigDecimal actualCost;
    /** Quy mô đối tượng mục tiêu. */
    private Integer targetSize;
    /** Doanh số kỳ vọng. */
    private BigDecimal expectedRevenue;
    /** ID người phụ trách. */
    private Long ownerId;
    /** Mô tả. */
    private String description;
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
