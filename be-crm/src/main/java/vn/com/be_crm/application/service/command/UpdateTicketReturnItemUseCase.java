package vn.com.be_crm.application.service.command;

import vn.com.be_crm.application.service.dto.TicketReturnItemResult;
import vn.com.be_crm.application.service.dto.UpdateTicketReturnItemCommand;
import vn.com.be_crm.application.service.mapper.TicketReturnItemCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.service.entity.TicketReturnItem;
import vn.com.be_crm.domain.service.repository.ITicketReturnItemRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case cập nhật dòng hàng trả/đổi. */
public class UpdateTicketReturnItemUseCase implements IUseCase<UpdateTicketReturnItemCommand, TicketReturnItemResult> {
    private final ITicketReturnItemRepository repo;
    /** @param repo port lưu trữ */
    public UpdateTicketReturnItemUseCase(ITicketReturnItemRepository repo) { this.repo = repo; }
    /** Cập nhật TicketReturnItem. @param cmd @return TicketReturnItemResult @throws NotFoundException */
    @Override public TicketReturnItemResult execute(UpdateTicketReturnItemCommand cmd) {
        TicketReturnItem e = repo.findById(cmd.getId())
                .orElseThrow(() -> new NotFoundException("TicketReturnItem not found: " + cmd.getId()));
        return TicketReturnItemCommandMapper.toResult(repo.save(TicketReturnItemCommandMapper.toEntity(cmd, e)));
    }
}
