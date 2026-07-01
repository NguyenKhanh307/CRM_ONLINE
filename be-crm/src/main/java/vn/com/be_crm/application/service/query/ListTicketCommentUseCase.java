package vn.com.be_crm.application.service.query;

import vn.com.be_crm.application.service.dto.TicketCommentResult;
import vn.com.be_crm.application.service.mapper.TicketCommentMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.service.repository.ITicketCommentRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy danh sách ghi chú / lịch sử theo ticketId. */
public class ListTicketCommentUseCase implements IUseCase<Long, List<TicketCommentResult>> {
    private final ITicketCommentRepository repo;
    /** @param repo port lưu trữ */
    public ListTicketCommentUseCase(ITicketCommentRepository repo) { this.repo = repo; }
    /** Lấy danh sách TicketComment theo ticketId. @param ticketId @return danh sách */
    @Override public List<TicketCommentResult> execute(Long ticketId) {
        return repo.findAllByTicketId(ticketId).stream()
                .map(TicketCommentMapper::toResult).collect(Collectors.toList());
    }
}
