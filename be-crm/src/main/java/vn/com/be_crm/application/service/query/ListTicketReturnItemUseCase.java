package vn.com.be_crm.application.service.query;

import vn.com.be_crm.application.service.dto.TicketReturnItemResult;
import vn.com.be_crm.application.service.mapper.TicketReturnItemCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.service.repository.ITicketReturnItemRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy danh sách dòng hàng trả/đổi theo ticketId. */
public class ListTicketReturnItemUseCase implements IUseCase<Long, List<TicketReturnItemResult>> {
    private final ITicketReturnItemRepository repo;
    /** @param repo port lưu trữ */
    public ListTicketReturnItemUseCase(ITicketReturnItemRepository repo) { this.repo = repo; }
    /** Lấy danh sách TicketReturnItem theo ticketId. @param ticketId @return danh sách */
    @Override public List<TicketReturnItemResult> execute(Long ticketId) {
        return repo.findAllByTicketId(ticketId).stream()
                .map(TicketReturnItemCommandMapper::toResult).collect(Collectors.toList());
    }
}
