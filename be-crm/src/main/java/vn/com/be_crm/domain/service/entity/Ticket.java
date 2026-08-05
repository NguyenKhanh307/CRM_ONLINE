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

// phiếu hỗ trợ / yêu cầu sau bán (support_tickets). Không còn customerId/contactId/invoiceId/
// productId trực tiếp — chỉ còn orderId, khách hàng/liên hệ/sản phẩm tra qua đơn hàng nếu cần.
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    private Long id;
    private String code;
    // loại yêu cầu: support | return | exchange | complaint
    private TicketType type;
    private String subject;
    private String description;
    // đơn hàng liên quan (cho trả/đổi hàng)
    private Long orderId;
    private TicketChannel channel;
    private TicketPriority priority;
    private TicketStatus status;
    private ReturnReason reason;
    private ResolutionType resolutionType;
    private String resolutionNote;
    private Long assignedUserId;
    private Long slaPolicyId;
    // thời điểm phản hồi đầu tiên (đo FRT)
    private LocalDateTime firstResponseAt;
    private LocalDateTime slaDueAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;
    private Integer satisfactionScore;
    private String satisfactionComment;
    // createdBy/updatedBy do BE tự đóng dấu (audit), client không gửi lên
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long deletedBy;
    private boolean isPurged;
}
