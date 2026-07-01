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

/** Chuyển đổi Command ↔ Ticket ↔ TicketResult. */
public class TicketCommandMapper {

    /**
     * Tạo Ticket từ CreateTicketCommand (áp mặc định: status=new, channel=web, priority=medium, type=support).
     * @param cmd command tạo mới @return domain entity
     */
    public static Ticket toEntity(CreateTicketCommand cmd) {
        return Ticket.builder()
                .code(cmd.getCode())
                .type(cmd.getType() != null ? cmd.getType() : TicketType.support)
                .subject(cmd.getSubject()).description(cmd.getDescription())
                .customerId(cmd.getCustomerId()).contactId(cmd.getContactId())
                .invoiceId(cmd.getInvoiceId()).productId(cmd.getProductId())
                .channel(cmd.getChannel() != null ? cmd.getChannel() : TicketChannel.web)
                .priority(cmd.getPriority() != null ? cmd.getPriority() : TicketPriority.medium)
                .status(TicketStatus.new_)
                .reason(cmd.getReason()).assignedUserId(cmd.getAssignedUserId())
                .build();
    }

    /**
     * Cập nhật Ticket từ UpdateTicketCommand — giữ nguyên status và các mốc SLA/thời gian (đổi qua hành động).
     * @param cmd command cập nhật @param e entity hiện tại @return domain entity đã cập nhật
     */
    public static Ticket toEntity(UpdateTicketCommand cmd, Ticket e) {
        return e.toBuilder()
                .type(cmd.getType() != null ? cmd.getType() : e.getType())
                .subject(cmd.getSubject() != null ? cmd.getSubject() : e.getSubject())
                .description(cmd.getDescription() != null ? cmd.getDescription() : e.getDescription())
                .customerId(cmd.getCustomerId() != null ? cmd.getCustomerId() : e.getCustomerId())
                .contactId(cmd.getContactId() != null ? cmd.getContactId() : e.getContactId())
                .invoiceId(cmd.getInvoiceId() != null ? cmd.getInvoiceId() : e.getInvoiceId())
                .productId(cmd.getProductId() != null ? cmd.getProductId() : e.getProductId())
                .channel(cmd.getChannel() != null ? cmd.getChannel() : e.getChannel())
                .priority(cmd.getPriority() != null ? cmd.getPriority() : e.getPriority())
                .reason(cmd.getReason() != null ? cmd.getReason() : e.getReason())
                .assignedUserId(cmd.getAssignedUserId() != null ? cmd.getAssignedUserId() : e.getAssignedUserId())
                // Trạng thái KHÔNG nhận từ command — chỉ đổi qua hành động.
                .status(e.getStatus())
                .build();
    }

    /** Trạng thái coi như "chưa xong" khi tính quá hạn SLA. */
    private static final Set<TicketStatus> OPEN_STATUSES = Set.of(
            TicketStatus.new_, TicketStatus.assigned, TicketStatus.in_progress,
            TicketStatus.approved, TicketStatus.received, TicketStatus.inspected, TicketStatus.reopened);

    /**
     * Chuyển Ticket sang TicketResult; suy ra cờ quá hạn SLA (không lưu DB).
     * @param e domain entity @return result DTO
     */
    public static TicketResult toResult(Ticket e) {
        boolean overdue = e.getSlaDueAt() != null
                && e.getSlaDueAt().isBefore(LocalDateTime.now())
                && OPEN_STATUSES.contains(e.getStatus());
        return TicketResult.builder()
                .id(e.getId()).code(e.getCode()).type(e.getType()).subject(e.getSubject())
                .description(e.getDescription()).customerId(e.getCustomerId()).contactId(e.getContactId())
                .invoiceId(e.getInvoiceId()).productId(e.getProductId()).channel(e.getChannel())
                .priority(e.getPriority()).status(e.getStatus()).reason(e.getReason())
                .resolutionType(e.getResolutionType()).resolutionNote(e.getResolutionNote())
                .assignedUserId(e.getAssignedUserId()).slaPolicyId(e.getSlaPolicyId())
                .firstResponseAt(e.getFirstResponseAt()).slaDueAt(e.getSlaDueAt())
                .resolvedAt(e.getResolvedAt()).closedAt(e.getClosedAt())
                .satisfactionScore(e.getSatisfactionScore()).satisfactionComment(e.getSatisfactionComment())
                .isOverdue(overdue)
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt())
                .build();
    }

    private TicketCommandMapper() {}
}
