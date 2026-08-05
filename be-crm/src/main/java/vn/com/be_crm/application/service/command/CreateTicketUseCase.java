package vn.com.be_crm.application.service.command;

import vn.com.be_crm.application.service.dto.CreateTicketCommand;
import vn.com.be_crm.application.service.dto.TicketResult;
import vn.com.be_crm.application.service.mapper.TicketCommandMapper;
import vn.com.be_crm.application.service.mapper.TicketReturnItemCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.service.entity.SlaPolicy;
import vn.com.be_crm.domain.service.entity.Ticket;
import vn.com.be_crm.domain.service.entity.TicketReturnItem;
import vn.com.be_crm.domain.service.repository.ISlaPolicyRepository;
import vn.com.be_crm.domain.service.repository.ITicketRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// tạo mới phiếu hỗ trợ: tra chính sách SLA theo độ ưu tiên để tính hạn giải quyết, lưu phiếu
// (kèm dòng hàng trả/đổi nếu có)
public class CreateTicketUseCase implements IUseCase<CreateTicketCommand, TicketResult> {
    private final ITicketRepository repo;
    private final ISlaPolicyRepository slaRepo;

    public CreateTicketUseCase(ITicketRepository repo, ISlaPolicyRepository slaRepo) {
        this.repo = repo; this.slaRepo = slaRepo;
    }

    @Override public TicketResult execute(CreateTicketCommand cmd) {
        Ticket entity = TicketCommandMapper.toEntity(cmd);
        // tra SLA theo độ ưu tiên -> set slaPolicyId + hạn giải quyết (createdAt + resolutionHours)
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

        return TicketCommandMapper.toResult(saved);
    }
}
