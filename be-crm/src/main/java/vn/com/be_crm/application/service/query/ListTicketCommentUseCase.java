package vn.com.be_crm.application.service.query;

import vn.com.be_crm.application.service.dto.TicketCommentResult;
import vn.com.be_crm.application.service.mapper.TicketCommentMapper;
import vn.com.be_crm.application.shared.lookup.INameResolver;
import vn.com.be_crm.application.shared.lookup.NameEnricher;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.service.repository.ITicketCommentRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy danh sách ghi chú / lịch sử theo ticketId. */
public class ListTicketCommentUseCase implements IUseCase<Long, List<TicketCommentResult>> {
    private final ITicketCommentRepository repo;
    private final INameResolver names;
    /** @param repo port lưu trữ @param names port tra tên khóa ngoại */
    public ListTicketCommentUseCase(ITicketCommentRepository repo, INameResolver names) { this.repo = repo; this.names = names; }
    /** Lấy danh sách TicketComment theo ticketId, kèm tên tác giả. @param ticketId @return danh sách */
    @Override public List<TicketCommentResult> execute(Long ticketId) {
        List<TicketCommentResult> items = repo.findAllByTicketId(ticketId).stream()
                .map(TicketCommentMapper::toResult).collect(Collectors.toList());
        NameEnricher.apply(items, TicketCommentResult::getAuthorId, names::users, TicketCommentResult::setAuthorName);
        return items;
    }
}
