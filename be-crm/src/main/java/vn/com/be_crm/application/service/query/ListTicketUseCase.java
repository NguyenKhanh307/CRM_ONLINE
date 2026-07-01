package vn.com.be_crm.application.service.query;

import vn.com.be_crm.application.service.dto.TicketResult;
import vn.com.be_crm.application.service.mapper.TicketCommandMapper;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.service.repository.ITicketRepository;

import java.util.stream.Collectors;

/** Use case lấy danh sách phiếu có phân trang. */
public class ListTicketUseCase implements IUseCase<PageRequest, PageResult<TicketResult>> {
    private final ITicketRepository repo;
    /** @param repo port lưu trữ */
    public ListTicketUseCase(ITicketRepository repo) { this.repo = repo; }
    /** Lấy danh sách Ticket. @param r phân trang @return PageResult */
    @Override public PageResult<TicketResult> execute(PageRequest r) {
        var page = repo.findAll(r);
        return PageResult.<TicketResult>builder()
                .items(page.getItems().stream().map(TicketCommandMapper::toResult).collect(Collectors.toList()))
                .total(page.getTotal()).page(page.getPage()).size(page.getSize()).build();
    }
}
