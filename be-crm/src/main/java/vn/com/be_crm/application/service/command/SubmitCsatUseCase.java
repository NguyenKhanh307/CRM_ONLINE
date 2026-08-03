package vn.com.be_crm.application.service.command;

import vn.com.be_crm.application.service.dto.TicketResult;
import vn.com.be_crm.application.service.mapper.TicketCommandMapper;
import vn.com.be_crm.domain.service.entity.Ticket;
import vn.com.be_crm.domain.service.enums.TicketStatus;
import vn.com.be_crm.domain.service.repository.ITicketRepository;
import vn.com.be_crm.core.error.frontend.DomainException;
import vn.com.be_crm.core.error.frontend.NotFoundException;

import java.util.Set;

/** Use case ghi nhận đánh giá hài lòng (CSAT) — chỉ khi phiếu đã resolved/closed. */
public class SubmitCsatUseCase {
    /** Trạng thái cho phép ghi CSAT. */
    private static final Set<TicketStatus> CSAT_STATUSES = Set.of(TicketStatus.resolved, TicketStatus.closed);

    private final ITicketRepository repo;
    /** @param repo port lưu trữ */
    public SubmitCsatUseCase(ITicketRepository repo) { this.repo = repo; }

    /**
     * Ghi điểm hài lòng (1-5) và nhận xét cho phiếu.
     * @param ticketId ID phiếu @param score điểm 1-5 @param comment nhận xét @return phiếu sau cập nhật
     */
    public TicketResult execute(Long ticketId, Integer score, String comment) {
        Ticket t = repo.findById(ticketId)
                .orElseThrow(() -> new NotFoundException("Ticket not found: " + ticketId));
        if (!CSAT_STATUSES.contains(t.getStatus())) {
            throw new DomainException("Chỉ đánh giá được phiếu đã giải quyết hoặc đã đóng");
        }
        if (score != null && (score < 1 || score > 5)) {
            throw new DomainException("Điểm hài lòng phải trong khoảng 1-5");
        }
        Ticket saved = repo.save(t.toBuilder().satisfactionScore(score).satisfactionComment(comment).build());
        return TicketCommandMapper.toResult(saved);
    }
}
