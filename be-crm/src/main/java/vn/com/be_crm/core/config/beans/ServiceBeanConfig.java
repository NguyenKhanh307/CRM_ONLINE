package vn.com.be_crm.core.config.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.be_crm.application.notification.command.CreateNotificationUseCase;
import vn.com.be_crm.application.service.command.*;
import vn.com.be_crm.application.service.query.*;
import vn.com.be_crm.domain.service.repository.ISlaPolicyRepository;
import vn.com.be_crm.domain.service.repository.ITicketRepository;
import vn.com.be_crm.domain.service.repository.ITicketReturnItemRepository;

// wire các UseCase của module Service (ticket, return-item, workflow, csat, SLA, trash, handover)
@Configuration
public class ServiceBeanConfig {

    // ===== Ticket CRUD =====

    @Bean public CreateTicketUseCase createTicketUseCase(ITicketRepository r, ISlaPolicyRepository sr) { return new CreateTicketUseCase(r, sr); }
    @Bean public UpdateTicketUseCase updateTicketUseCase(ITicketRepository r) { return new UpdateTicketUseCase(r); }
    @Bean public DeleteTicketUseCase deleteTicketUseCase(ITicketRepository r) { return new DeleteTicketUseCase(r); }
    @Bean public GetTicketUseCase getTicketUseCase(ITicketRepository r) { return new GetTicketUseCase(r); }
    @Bean public ListTicketUseCase listTicketUseCase(ITicketRepository r, vn.com.be_crm.core.lookup.port.INameResolver n) { return new ListTicketUseCase(r, n); }
    @Bean public ImportBulkTicketUseCase importBulkTicketUseCase(ITicketRepository r) { return new ImportBulkTicketUseCase(r); }

    // ===== Workflow / CSAT =====

    // luồng assign/start/resolve/approve/reject/receive/inspect/complete/close/reopen
    @Bean public TicketWorkflowUseCase ticketWorkflowUseCase(ITicketRepository r, CreateNotificationUseCase nuc) {
        return new TicketWorkflowUseCase(r, nuc);
    }
    @Bean public SubmitCsatUseCase submitCsatUseCase(ITicketRepository r) { return new SubmitCsatUseCase(r); }

    // ===== Trang public (không JWT) — support-page theo mã phiếu =====

    @Bean public GetTicketByCodePublicUseCase getTicketByCodePublicUseCase(ITicketRepository r) {
        return new GetTicketByCodePublicUseCase(r);
    }
    @Bean public SubmitCsatByCodeUseCase submitCsatByCodeUseCase(ITicketRepository r, SubmitCsatUseCase submitCsatUseCase) {
        return new SubmitCsatByCodeUseCase(r, submitCsatUseCase);
    }

    // ===== Return items =====

    @Bean public CreateTicketReturnItemUseCase createTicketReturnItemUseCase(ITicketReturnItemRepository r, ITicketRepository tr) { return new CreateTicketReturnItemUseCase(r, tr); }
    @Bean public UpdateTicketReturnItemUseCase updateTicketReturnItemUseCase(ITicketReturnItemRepository r, ITicketRepository tr) { return new UpdateTicketReturnItemUseCase(r, tr); }
    @Bean public DeleteTicketReturnItemUseCase deleteTicketReturnItemUseCase(ITicketReturnItemRepository r, ITicketRepository tr) { return new DeleteTicketReturnItemUseCase(r, tr); }
    @Bean public ListTicketReturnItemUseCase listTicketReturnItemUseCase(ITicketReturnItemRepository r) { return new ListTicketReturnItemUseCase(r); }

    // ===== SLA =====

    @Bean public ListSlaPolicyUseCase listSlaPolicyUseCase(ISlaPolicyRepository r) { return new ListSlaPolicyUseCase(r); }

    // ===== Trash & Handover =====

    @Bean public ListDeletedTicketsUseCase listDeletedTicketsUseCase(ITicketRepository r) { return new ListDeletedTicketsUseCase(r); }
    @Bean public RestoreTicketUseCase restoreTicketUseCase(ITicketRepository r) { return new RestoreTicketUseCase(r); }
    @Bean public PurgeTicketUseCase purgeTicketUseCase(ITicketRepository r) { return new PurgeTicketUseCase(r); }
    @Bean public HandoverBulkTicketUseCase handoverBulkTicketUseCase(ITicketRepository r) { return new HandoverBulkTicketUseCase(r); }
}
