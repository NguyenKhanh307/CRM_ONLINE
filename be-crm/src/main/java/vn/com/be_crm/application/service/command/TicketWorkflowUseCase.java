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

// điều phối luồng trạng thái phiếu hỗ trợ (theo hành động, không sửa tay). Mỗi bước: kiểm tra
// guard chuyển trạng thái -> lưu -> thông báo (nếu cần). Không còn ghi audit log riêng (bảng
// ticket_comments đã bỏ) — lịch sử chuyển trạng thái chỉ còn suy ra từ updated_at.
public class TicketWorkflowUseCase {
    // các loại yêu cầu áp dụng luồng trả/đổi (duyệt + kiểm hàng)
    private static final Set<TicketType> RETURN_TYPES = Set.of(TicketType.return_, TicketType.exchange);
    // các loại yêu cầu áp dụng luồng hỗ trợ (giải quyết trực tiếp)
    private static final Set<TicketType> SUPPORT_TYPES = Set.of(TicketType.support, TicketType.complaint);

    private final ITicketRepository repo;
    private final CreateNotificationUseCase createNotificationUC;

    public TicketWorkflowUseCase(ITicketRepository repo, CreateNotificationUseCase createNotificationUC) {
        this.repo = repo; this.createNotificationUC = createNotificationUC;
    }

    // giao phiếu cho nhân viên xử lý: new -> assigned
    public TicketResult assign(Long ticketId, Long toUserId) {
        Ticket t = load(ticketId);
        t.getStatus().ensureCanTransitionTo(TicketStatus.assigned);
        Ticket saved = repo.save(t.toBuilder().status(TicketStatus.assigned).assignedUserId(toUserId).build());
        if (toUserId != null) {
            createNotificationUC.execute(List.of(toUserId), "ticket_assigned",
                    "Phiếu được giao cho bạn: " + saved.getCode(),
                    "Bạn được giao xử lý phiếu " + saved.getCode() + " — " + saved.getSubject(), "ticket", saved.getId());
        }
        return TicketCommandMapper.toResult(saved);
    }

    // bắt đầu xử lý: assigned/reopened -> in_progress, ghi mốc phản hồi đầu tiên (FRT).
    // support/complaint được bắt đầu thẳng từ new (bỏ bước giao việc) vì người tạo đã mặc định
    // là người phụ trách; return/exchange vẫn phải qua assigned trước (giữ nguyên bước xác minh)
    public TicketResult start(Long ticketId) {
        Ticket t = load(ticketId);
        if (t.getStatus() == TicketStatus.new_) {
            requireType(t, SUPPORT_TYPES, "bắt đầu xử lý trực tiếp (bỏ qua bước giao việc)");
        }
        t.getStatus().ensureCanTransitionTo(TicketStatus.in_progress);
        Ticket saved = repo.save(t.toBuilder()
                .status(TicketStatus.in_progress)
                .firstResponseAt(t.getFirstResponseAt() != null ? t.getFirstResponseAt() : LocalDateTime.now())
                .build());
        return TicketCommandMapper.toResult(saved);
    }

    // giải quyết xong (support/complaint): in_progress -> resolved, ghi hình thức giải quyết
    public TicketResult resolve(Long ticketId, ResolutionType resolutionType, String note) {
        Ticket t = load(ticketId);
        requireType(t, SUPPORT_TYPES, "giải quyết trực tiếp");
        t.getStatus().ensureCanTransitionTo(TicketStatus.resolved);
        Ticket saved = repo.save(t.toBuilder()
                .status(TicketStatus.resolved).resolutionType(resolutionType).resolutionNote(note)
                .resolvedAt(LocalDateTime.now()).build());
        notifyAssignee(saved, "ticket_resolved", "Phiếu đã được giải quyết: " + saved.getCode(),
                "Phiếu " + saved.getCode() + " đã được giải quyết.");
        return TicketCommandMapper.toResult(saved);
    }

    // duyệt yêu cầu trả/đổi: in_progress -> approved
    public TicketResult approve(Long ticketId, String note) {
        Ticket t = load(ticketId);
        requireType(t, RETURN_TYPES, "duyệt trả/đổi");
        t.getStatus().ensureCanTransitionTo(TicketStatus.approved);
        Ticket saved = repo.save(t.toBuilder()
                .status(TicketStatus.approved).resolutionNote(note != null ? note : t.getResolutionNote()).build());
        return TicketCommandMapper.toResult(saved);
    }

    // từ chối yêu cầu trả/đổi: in_progress -> rejected, ghi lý do
    public TicketResult reject(Long ticketId, String reason) {
        Ticket t = load(ticketId);
        requireType(t, RETURN_TYPES, "từ chối trả/đổi");
        t.getStatus().ensureCanTransitionTo(TicketStatus.rejected);
        Ticket saved = repo.save(t.toBuilder()
                .status(TicketStatus.rejected).resolutionType(ResolutionType.rejected).resolutionNote(reason).build());
        return TicketCommandMapper.toResult(saved);
    }

    // ghi nhận đã nhận hàng trả/đổi: approved -> received
    public TicketResult receive(Long ticketId) {
        Ticket t = load(ticketId);
        requireType(t, RETURN_TYPES, "nhận hàng");
        t.getStatus().ensureCanTransitionTo(TicketStatus.received);
        Ticket saved = repo.save(t.toBuilder().status(TicketStatus.received).build());
        return TicketCommandMapper.toResult(saved);
    }

    // ghi nhận đã kiểm hàng: received -> inspected
    public TicketResult inspect(Long ticketId) {
        Ticket t = load(ticketId);
        requireType(t, RETURN_TYPES, "kiểm hàng");
        t.getStatus().ensureCanTransitionTo(TicketStatus.inspected);
        Ticket saved = repo.save(t.toBuilder().status(TicketStatus.inspected).build());
        return TicketCommandMapper.toResult(saved);
    }

    // hoàn tất xử lý trả/đổi: inspected -> resolved, ghi hình thức giải quyết
    public TicketResult complete(Long ticketId, ResolutionType resolutionType, String note) {
        Ticket t = load(ticketId);
        requireType(t, RETURN_TYPES, "hoàn tất trả/đổi");
        t.getStatus().ensureCanTransitionTo(TicketStatus.resolved);
        Ticket saved = repo.save(t.toBuilder()
                .status(TicketStatus.resolved)
                .resolutionType(resolutionType != null ? resolutionType : t.getResolutionType())
                .resolutionNote(note != null ? note : t.getResolutionNote())
                .resolvedAt(LocalDateTime.now()).build());
        notifyAssignee(saved, "ticket_resolved", "Phiếu đã được giải quyết: " + saved.getCode(),
                "Phiếu " + saved.getCode() + " đã hoàn tất xử lý.");
        return TicketCommandMapper.toResult(saved);
    }

    // đóng phiếu: resolved/rejected -> closed
    public TicketResult close(Long ticketId) {
        Ticket t = load(ticketId);
        t.getStatus().ensureCanTransitionTo(TicketStatus.closed);
        Ticket saved = repo.save(t.toBuilder().status(TicketStatus.closed).closedAt(LocalDateTime.now()).build());
        return TicketCommandMapper.toResult(saved);
    }

    // mở lại phiếu: resolved/closed -> reopened
    public TicketResult reopen(Long ticketId) {
        Ticket t = load(ticketId);
        t.getStatus().ensureCanTransitionTo(TicketStatus.reopened);
        Ticket saved = repo.save(t.toBuilder().status(TicketStatus.reopened).build());
        return TicketCommandMapper.toResult(saved);
    }

    private Ticket load(Long id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Ticket not found: " + id));
    }

    // ràng buộc loại phiếu áp dụng cho một hành động; ném DomainException nếu không hợp lệ
    private void requireType(Ticket t, Set<TicketType> allowed, String action) {
        if (!allowed.contains(t.getType())) {
            throw new DomainException("Loại phiếu '" + t.getType().toJson() + "' không áp dụng hành động " + action);
        }
    }

    // gửi thông báo cho nhân viên đang xử lý phiếu (nếu có)
    private void notifyAssignee(Ticket t, String type, String title, String content) {
        if (t.getAssignedUserId() == null) return;
        createNotificationUC.execute(List.of(t.getAssignedUserId()), type, title, content, "ticket", t.getId());
    }
}
