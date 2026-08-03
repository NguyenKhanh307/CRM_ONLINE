package vn.com.be_crm.application.service.command;

import vn.com.be_crm.application.notification.command.CreateNotificationUseCase;
import vn.com.be_crm.application.service.dto.TicketResult;
import vn.com.be_crm.application.service.mapper.TicketCommandMapper;
import vn.com.be_crm.domain.service.entity.Ticket;
import vn.com.be_crm.domain.service.enums.ResolutionType;
import vn.com.be_crm.domain.service.enums.TicketStatus;
import vn.com.be_crm.domain.service.enums.TicketType;
import vn.com.be_crm.domain.service.repository.ITicketRepository;
import vn.com.be_crm.core.error.frontend.DomainException;
import vn.com.be_crm.core.error.frontend.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Use case điều phối luồng trạng thái phiếu hỗ trợ (theo hành động, không sửa tay).
 * Mỗi bước: kiểm tra guard chuyển trạng thái → lưu → ghi audit log → thông báo (nếu cần).
 */
public class TicketWorkflowUseCase {
    /** Các loại yêu cầu áp dụng luồng trả/đổi (duyệt + kiểm hàng). */
    private static final Set<TicketType> RETURN_TYPES = Set.of(TicketType.return_, TicketType.exchange);
    /** Các loại yêu cầu áp dụng luồng hỗ trợ (giải quyết trực tiếp). */
    private static final Set<TicketType> SUPPORT_TYPES = Set.of(TicketType.support, TicketType.complaint);

    private final ITicketRepository repo;
    private final LogTicketEventUseCase logEvent;
    private final CreateNotificationUseCase createNotificationUC;

    /** @param repo phiếu @param logEvent ghi audit log @param createNotificationUC tạo thông báo */
    public TicketWorkflowUseCase(ITicketRepository repo, LogTicketEventUseCase logEvent,
                                 CreateNotificationUseCase createNotificationUC) {
        this.repo = repo; this.logEvent = logEvent; this.createNotificationUC = createNotificationUC;
    }

    /**
     * Giao phiếu cho nhân viên xử lý: new → assigned.
     * @param ticketId ID phiếu @param toUserId người nhận @return phiếu sau cập nhật
     */
    public TicketResult assign(Long ticketId, Long toUserId) {
        Ticket t = load(ticketId);
        t.getStatus().ensureCanTransitionTo(TicketStatus.assigned);
        Ticket saved = repo.save(t.toBuilder().status(TicketStatus.assigned).assignedUserId(toUserId).build());
        logEvent.execute(ticketId, "Giao phiếu cho người dùng #" + toUserId);
        if (toUserId != null) {
            createNotificationUC.execute(List.of(toUserId), "ticket_assigned",
                    "Phiếu được giao cho bạn: " + saved.getCode(),
                    "Bạn được giao xử lý phiếu " + saved.getCode() + " — " + saved.getSubject(), null, saved.getId());
        }
        return TicketCommandMapper.toResult(saved);
    }

    /**
     * Bắt đầu xử lý: assigned/reopened → in_progress, ghi mốc phản hồi đầu tiên (FRT).
     * @param ticketId ID phiếu @return phiếu sau cập nhật
     */
    public TicketResult start(Long ticketId) {
        Ticket t = load(ticketId);
        t.getStatus().ensureCanTransitionTo(TicketStatus.in_progress);
        Ticket saved = repo.save(t.toBuilder()
                .status(TicketStatus.in_progress)
                .firstResponseAt(t.getFirstResponseAt() != null ? t.getFirstResponseAt() : LocalDateTime.now())
                .build());
        logEvent.execute(ticketId, "Bắt đầu xử lý phiếu");
        return TicketCommandMapper.toResult(saved);
    }

    /**
     * Giải quyết xong (support/complaint): in_progress → resolved, ghi hình thức giải quyết.
     * @param ticketId ID phiếu @param resolutionType hình thức @param note ghi chú @return phiếu sau cập nhật
     */
    public TicketResult resolve(Long ticketId, ResolutionType resolutionType, String note) {
        Ticket t = load(ticketId);
        requireType(t, SUPPORT_TYPES, "giải quyết trực tiếp");
        t.getStatus().ensureCanTransitionTo(TicketStatus.resolved);
        Ticket saved = repo.save(t.toBuilder()
                .status(TicketStatus.resolved).resolutionType(resolutionType).resolutionNote(note)
                .resolvedAt(LocalDateTime.now()).build());
        logEvent.execute(ticketId, "Đã giải quyết phiếu" + (resolutionType != null ? " (" + resolutionType + ")" : ""));
        notifyAssignee(saved, "ticket_resolved", "Phiếu đã được giải quyết: " + saved.getCode(),
                "Phiếu " + saved.getCode() + " đã được giải quyết.");
        return TicketCommandMapper.toResult(saved);
    }

    /**
     * Duyệt yêu cầu trả/đổi: in_progress → approved.
     * @param ticketId ID phiếu @param note ghi chú duyệt @return phiếu sau cập nhật
     */
    public TicketResult approve(Long ticketId, String note) {
        Ticket t = load(ticketId);
        requireType(t, RETURN_TYPES, "duyệt trả/đổi");
        t.getStatus().ensureCanTransitionTo(TicketStatus.approved);
        Ticket saved = repo.save(t.toBuilder()
                .status(TicketStatus.approved).resolutionNote(note != null ? note : t.getResolutionNote()).build());
        logEvent.execute(ticketId, "Duyệt yêu cầu trả/đổi hàng");
        return TicketCommandMapper.toResult(saved);
    }

    /**
     * Từ chối yêu cầu trả/đổi: in_progress → rejected, ghi lý do.
     * @param ticketId ID phiếu @param reason lý do @return phiếu sau cập nhật
     */
    public TicketResult reject(Long ticketId, String reason) {
        Ticket t = load(ticketId);
        requireType(t, RETURN_TYPES, "từ chối trả/đổi");
        t.getStatus().ensureCanTransitionTo(TicketStatus.rejected);
        Ticket saved = repo.save(t.toBuilder()
                .status(TicketStatus.rejected).resolutionType(ResolutionType.rejected).resolutionNote(reason).build());
        logEvent.execute(ticketId, "Từ chối trả/đổi: " + (reason != null ? reason : "(không có lý do)"));
        return TicketCommandMapper.toResult(saved);
    }

    /**
     * Ghi nhận đã nhận hàng trả/đổi: approved → received.
     * @param ticketId ID phiếu @return phiếu sau cập nhật
     */
    public TicketResult receive(Long ticketId) {
        Ticket t = load(ticketId);
        requireType(t, RETURN_TYPES, "nhận hàng");
        t.getStatus().ensureCanTransitionTo(TicketStatus.received);
        Ticket saved = repo.save(t.toBuilder().status(TicketStatus.received).build());
        logEvent.execute(ticketId, "Đã nhận hàng, chuyển bước kiểm tra");
        return TicketCommandMapper.toResult(saved);
    }

    /**
     * Ghi nhận đã kiểm hàng: received → inspected.
     * @param ticketId ID phiếu @return phiếu sau cập nhật
     */
    public TicketResult inspect(Long ticketId) {
        Ticket t = load(ticketId);
        requireType(t, RETURN_TYPES, "kiểm hàng");
        t.getStatus().ensureCanTransitionTo(TicketStatus.inspected);
        Ticket saved = repo.save(t.toBuilder().status(TicketStatus.inspected).build());
        logEvent.execute(ticketId, "Đã kiểm tra hàng");
        return TicketCommandMapper.toResult(saved);
    }

    /**
     * Hoàn tất xử lý trả/đổi: inspected → resolved, ghi hình thức giải quyết.
     * @param ticketId ID phiếu @param resolutionType hình thức @param note ghi chú @return phiếu sau cập nhật
     */
    public TicketResult complete(Long ticketId, ResolutionType resolutionType, String note) {
        Ticket t = load(ticketId);
        requireType(t, RETURN_TYPES, "hoàn tất trả/đổi");
        t.getStatus().ensureCanTransitionTo(TicketStatus.resolved);
        Ticket saved = repo.save(t.toBuilder()
                .status(TicketStatus.resolved)
                .resolutionType(resolutionType != null ? resolutionType : t.getResolutionType())
                .resolutionNote(note != null ? note : t.getResolutionNote())
                .resolvedAt(LocalDateTime.now()).build());
        logEvent.execute(ticketId, "Hoàn tất xử lý trả/đổi hàng");
        notifyAssignee(saved, "ticket_resolved", "Phiếu đã được giải quyết: " + saved.getCode(),
                "Phiếu " + saved.getCode() + " đã hoàn tất xử lý.");
        return TicketCommandMapper.toResult(saved);
    }

    /**
     * Đóng phiếu: resolved/rejected → closed.
     * @param ticketId ID phiếu @return phiếu sau cập nhật
     */
    public TicketResult close(Long ticketId) {
        Ticket t = load(ticketId);
        t.getStatus().ensureCanTransitionTo(TicketStatus.closed);
        Ticket saved = repo.save(t.toBuilder().status(TicketStatus.closed).closedAt(LocalDateTime.now()).build());
        logEvent.execute(ticketId, "Đóng phiếu");
        return TicketCommandMapper.toResult(saved);
    }

    /**
     * Mở lại phiếu: resolved/closed → reopened.
     * @param ticketId ID phiếu @return phiếu sau cập nhật
     */
    public TicketResult reopen(Long ticketId) {
        Ticket t = load(ticketId);
        t.getStatus().ensureCanTransitionTo(TicketStatus.reopened);
        Ticket saved = repo.save(t.toBuilder().status(TicketStatus.reopened).build());
        logEvent.execute(ticketId, "Mở lại phiếu");
        return TicketCommandMapper.toResult(saved);
    }

    /** Tải phiếu theo ID hoặc ném NotFoundException. */
    private Ticket load(Long id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Ticket not found: " + id));
    }

    /** Ràng buộc loại phiếu áp dụng cho một hành động; ném DomainException nếu không hợp lệ. */
    private void requireType(Ticket t, Set<TicketType> allowed, String action) {
        if (!allowed.contains(t.getType())) {
            throw new DomainException("Loại phiếu '" + t.getType().toJson() + "' không áp dụng hành động " + action);
        }
    }

    /** Gửi thông báo cho nhân viên đang xử lý phiếu (nếu có). */
    private void notifyAssignee(Ticket t, String type, String title, String content) {
        if (t.getAssignedUserId() == null) return;
        createNotificationUC.execute(List.of(t.getAssignedUserId()), type, title, content, null, t.getId());
    }
}
