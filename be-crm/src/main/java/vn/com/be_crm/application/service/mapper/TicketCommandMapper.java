package vn.com.be_crm.application.service.mapper;

import vn.com.be_crm.application.service.dto.CreateTicketCommand;
import vn.com.be_crm.application.service.dto.TicketResult;
import vn.com.be_crm.application.service.dto.UpdateTicketCommand;
import vn.com.be_crm.domain.service.entity.Ticket;
import vn.com.be_crm.domain.service.enums.TicketChannel;
import vn.com.be_crm.domain.service.enums.TicketPriority;
import vn.com.be_crm.domain.service.enums.TicketStatus;
import vn.com.be_crm.domain.service.enums.TicketType;

import java.time.LocalDateTime;
import java.util.Set;

// chuyển đổi Command <-> Ticket <-> TicketResult
public class TicketCommandMapper {

    // tạo Ticket từ CreateTicketCommand (áp mặc định: status=new, channel=web, priority=medium, type=support)
    public static Ticket toEntity(CreateTicketCommand cmd) {
        return Ticket.builder()
                .code(cmd.getCode())
                .type(cmd.getType() != null ? cmd.getType() : TicketType.support)
                .subject(cmd.getSubject()).description(cmd.getDescription())
                .orderId(cmd.getOrderId())
                .channel(cmd.getChannel() != null ? cmd.getChannel() : TicketChannel.web)
                .priority(cmd.getPriority() != null ? cmd.getPriority() : TicketPriority.medium)
                .status(TicketStatus.new_)
                .reason(cmd.getReason()).assignedUserId(cmd.getAssignedUserId())
                .build();
    }

    // giữ nguyên status và các mốc SLA/thời gian (đổi qua hành động)
    public static Ticket toEntity(UpdateTicketCommand cmd, Ticket e) {
        return e.toBuilder()
                .type(cmd.getType() != null ? cmd.getType() : e.getType())
                .subject(cmd.getSubject() != null ? cmd.getSubject() : e.getSubject())
                .description(cmd.getDescription() != null ? cmd.getDescription() : e.getDescription())
                .orderId(cmd.getOrderId() != null ? cmd.getOrderId() : e.getOrderId())
                .channel(cmd.getChannel() != null ? cmd.getChannel() : e.getChannel())
                .priority(cmd.getPriority() != null ? cmd.getPriority() : e.getPriority())
                .reason(cmd.getReason() != null ? cmd.getReason() : e.getReason())
                .assignedUserId(cmd.getAssignedUserId() != null ? cmd.getAssignedUserId() : e.getAssignedUserId())
                // trạng thái KHÔNG nhận từ command — chỉ đổi qua hành động
                .status(e.getStatus())
                .build();
    }

    // trạng thái coi như "chưa xong" khi tính quá hạn SLA
    private static final Set<TicketStatus> OPEN_STATUSES = Set.of(
            TicketStatus.new_, TicketStatus.assigned, TicketStatus.in_progress,
            TicketStatus.approved, TicketStatus.received, TicketStatus.inspected, TicketStatus.reopened);

    // suy ra cờ quá hạn SLA (không lưu DB)
    public static TicketResult toResult(Ticket e) {
        boolean overdue = e.getSlaDueAt() != null
                && e.getSlaDueAt().isBefore(LocalDateTime.now())
                && OPEN_STATUSES.contains(e.getStatus());
        return TicketResult.builder()
                .id(e.getId()).code(e.getCode()).type(e.getType()).subject(e.getSubject())
                .description(e.getDescription()).orderId(e.getOrderId()).channel(e.getChannel())
                .priority(e.getPriority()).status(e.getStatus()).reason(e.getReason())
                .resolutionType(e.getResolutionType()).resolutionNote(e.getResolutionNote())
                .assignedUserId(e.getAssignedUserId()).slaPolicyId(e.getSlaPolicyId())
                .firstResponseAt(e.getFirstResponseAt()).slaDueAt(e.getSlaDueAt())
                .resolvedAt(e.getResolvedAt()).closedAt(e.getClosedAt())
                .satisfactionScore(e.getSatisfactionScore()).satisfactionComment(e.getSatisfactionComment())
                .isOverdue(overdue)
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt())
                .build();
    }

    private TicketCommandMapper() {}
}
