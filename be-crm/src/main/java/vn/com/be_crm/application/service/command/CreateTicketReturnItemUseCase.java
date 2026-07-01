package vn.com.be_crm.application.service.command;

import vn.com.be_crm.application.service.dto.CreateTicketReturnItemCommand;
import vn.com.be_crm.application.service.dto.TicketReturnItemResult;
import vn.com.be_crm.application.service.mapper.TicketReturnItemCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.service.repository.ITicketReturnItemRepository;

/** Use case tạo mới dòng hàng trả/đổi. */
public class CreateTicketReturnItemUseCase implements IUseCase<CreateTicketReturnItemCommand, TicketReturnItemResult> {
    private final ITicketReturnItemRepository repo;
    /** @param repo port lưu trữ */
    public CreateTicketReturnItemUseCase(ITicketReturnItemRepository repo) { this.repo = repo; }
    /** Tạo mới TicketReturnItem. @param cmd @return TicketReturnItemResult */
    @Override public TicketReturnItemResult execute(CreateTicketReturnItemCommand cmd) {
        return TicketReturnItemCommandMapper.toResult(repo.save(TicketReturnItemCommandMapper.toEntity(cmd)));
    }
}
