package vn.com.be_crm.application.service.query;

import vn.com.be_crm.application.service.dto.TicketResult;
import vn.com.be_crm.application.service.mapper.TicketCommandMapper;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.application.shared.lookup.INameResolver;
import vn.com.be_crm.application.shared.lookup.NameEnricher;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.service.repository.ITicketRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy danh sách phiếu có phân trang. */
public class ListTicketUseCase implements IUseCase<PageRequest, PageResult<TicketResult>> {
    private final ITicketRepository repo;
    private final INameResolver names;
    /** @param repo port lưu trữ @param names port tra tên khóa ngoại */
    public ListTicketUseCase(ITicketRepository repo, INameResolver names) { this.repo = repo; this.names = names; }
    /** Lấy danh sách Ticket kèm tên khóa ngoại (khách hàng, liên hệ, người xử lý). @param r phân trang @return PageResult */
    @Override public PageResult<TicketResult> execute(PageRequest r) {
        var page = repo.findAll(r);
        List<TicketResult> items = page.getItems().stream().map(TicketCommandMapper::toResult).collect(Collectors.toList());
        NameEnricher.apply(items, TicketResult::getCustomerId, names::customers, TicketResult::setCustomerName);
        NameEnricher.apply(items, TicketResult::getContactId, names::contacts, TicketResult::setContactName);
        NameEnricher.apply(items, TicketResult::getAssignedUserId, names::users, TicketResult::setAssignedUserName);
        return PageResult.<TicketResult>builder()
                .items(items)
                .total(page.getTotal()).page(page.getPage()).size(page.getSize()).build();
    }
}
