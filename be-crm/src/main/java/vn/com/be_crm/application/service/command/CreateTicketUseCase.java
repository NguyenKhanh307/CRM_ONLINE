package vn.com.be_crm.application.service.command;

import vn.com.be_crm.application.service.dto.CreateTicketCommand;
import vn.com.be_crm.application.service.dto.TicketResult;
import vn.com.be_crm.application.service.mapper.TicketCommandMapper;
import vn.com.be_crm.application.service.mapper.TicketReturnItemCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.service.entity.SlaPolicy;
import vn.com.be_crm.domain.service.entity.Ticket;
import vn.com.be_crm.domain.service.entity.TicketReturnItem;
import vn.com.be_crm.domain.service.repository.ISlaPolicyRepository;
import vn.com.be_crm.domain.service.repository.ITicketRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Use case tạo mới phiếu hỗ trợ: tra chính sách SLA theo độ ưu tiên để tính hạn giải quyết,
 * lưu phiếu (kèm dòng hàng trả/đổi nếu có), rồi ghi audit log "tạo phiếu".
 */
public class CreateTicketUseCase implements IUseCase<CreateTicketCommand, TicketResult> {
    private final ITicketRepository repo;
    private final ISlaPolicyRepository slaRepo;
    private final LogTicketEventUseCase logEvent;

    /** @param repo phiếu @param slaRepo chính sách SLA @param logEvent ghi audit log */
    public CreateTicketUseCase(ITicketRepository repo, ISlaPolicyRepository slaRepo, LogTicketEventUseCase logEvent) {
        this.repo = repo; this.slaRepo = slaRepo; this.logEvent = logEvent;
    }

    /** Tạo mới phiếu + tính SLA + lưu dòng hàng + log. @param cmd @return TicketResult */
    @Override public TicketResult execute(CreateTicketCommand cmd) {
        Ticket entity = TicketCommandMapper.toEntity(cmd);
        // Tra SLA theo độ ưu tiên → set slaPolicyId + hạn giải quyết (createdAt + resolutionHours)
        SlaPolicy sla = slaRepo.findByPriority(entity.getPriority()).orElse(null);
        if (sla != null) {
            entity = entity.toBuilder()
                    .slaPolicyId(sla.getId())
                    .slaDueAt(LocalDateTime.now().plusHours(sla.getResolutionHours()))
                    .build();
        }

        Ticket saved;
        if (cmd.getReturnItems() != null && !cmd.getReturnItems().isEmpty()) {
            List<TicketReturnItem> items = cmd.getReturnItems().stream()
                    .map(TicketReturnItemCommandMapper::toEntity).collect(Collectors.toList());
            saved = repo.saveWithReturnItems(entity, items);
        } else {
            saved = repo.save(entity);
        }

        logEvent.execute(saved.getId(), "Tạo phiếu " + saved.getCode() + " từ kênh " + saved.getChannel());
        return TicketCommandMapper.toResult(saved);
    }
}
