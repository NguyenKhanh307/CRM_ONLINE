package vn.com.be_crm.domain.service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.service.enums.ResolutionType;
import vn.com.be_crm.domain.service.enums.ReturnReason;
import vn.com.be_crm.domain.service.enums.TicketChannel;
import vn.com.be_crm.domain.service.enums.TicketPriority;
import vn.com.be_crm.domain.service.enums.TicketStatus;
import vn.com.be_crm.domain.service.enums.TicketType;

import java.time.LocalDateTime;

/**
 * Domain entity đại diện cho phiếu hỗ trợ / yêu cầu sau bán (support_tickets).
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    /** ID phiếu. */
    private Long id;
    /** Mã phiếu. */
    private String code;
    /** Loại yêu cầu: support | return | exchange | complaint. */
    private TicketType type;
    /** Tiêu đề. */
    private String subject;
    /** Mô tả chi tiết. */
    private String description;
    /** ID khách hàng. */
    private Long customerId;
    /** ID liên hệ. */
    private Long contactId;
    /** ID hóa đơn gốc (cho trả/đổi hàng). */
    private Long invoiceId;
    /** ID sản phẩm liên quan chính. */
    private Long productId;
    /** Kênh tiếp nhận. */
    private TicketChannel channel;
    /** Độ ưu tiên. */
    private TicketPriority priority;
    /** Trạng thái xử lý. */
    private TicketStatus status;
    /** Lý do trả/đổi/khiếu nại. */
    private ReturnReason reason;
    /** Hình thức giải quyết. */
    private ResolutionType resolutionType;
    /** Ghi chú giải quyết. */
    private String resolutionNote;
    /** ID nhân viên xử lý. */
    private Long assignedUserId;
    /** ID chính sách SLA áp dụng. */
    private Long slaPolicyId;
    /** Thời điểm phản hồi đầu tiên (đo FRT). */
    private LocalDateTime firstResponseAt;
    /** Hạn giải quyết theo SLA. */
    private LocalDateTime slaDueAt;
    /** Thời điểm giải quyết xong. */
    private LocalDateTime resolvedAt;
    /** Thời điểm đóng phiếu. */
    private LocalDateTime closedAt;
    /** Điểm hài lòng CSAT (1-5). */
    private Integer satisfactionScore;
    /** Nhận xét CSAT. */
    private String satisfactionComment;
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
