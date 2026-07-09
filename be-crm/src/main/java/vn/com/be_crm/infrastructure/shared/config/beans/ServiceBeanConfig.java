package vn.com.be_crm.infrastructure.shared.config.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.be_crm.application.notification.command.CreateNotificationUseCase;
import vn.com.be_crm.application.service.command.*;
import vn.com.be_crm.application.service.query.*;
import vn.com.be_crm.domain.service.repository.ISlaPolicyRepository;
import vn.com.be_crm.domain.service.repository.ITicketCommentRepository;
import vn.com.be_crm.domain.service.repository.ITicketRepository;
import vn.com.be_crm.domain.service.repository.ITicketReturnItemRepository;

/**
 * Wire các UseCase của module Service (ticket, return-item, comment, workflow, csat, SLA, trash, handover) qua @Bean.
 */
@Configuration
public class ServiceBeanConfig {

    // ===== Ticket CRUD =====

    /** @return LogTicketEventUseCase — ghi audit log system dùng chung */
    @Bean public LogTicketEventUseCase logTicketEventUseCase(ITicketCommentRepository cr) { return new LogTicketEventUseCase(cr); }
    /** @return CreateTicketUseCase */
    @Bean public CreateTicketUseCase createTicketUseCase(ITicketRepository r, ISlaPolicyRepository sr, LogTicketEventUseCase log) { return new CreateTicketUseCase(r, sr, log); }
    /** @return UpdateTicketUseCase */
    @Bean public UpdateTicketUseCase updateTicketUseCase(ITicketRepository r) { return new UpdateTicketUseCase(r); }
    /** @return DeleteTicketUseCase */
    @Bean public DeleteTicketUseCase deleteTicketUseCase(ITicketRepository r) { return new DeleteTicketUseCase(r); }
    /** @return GetTicketUseCase */
    @Bean public GetTicketUseCase getTicketUseCase(ITicketRepository r) { return new GetTicketUseCase(r); }
    /** @return ListTicketUseCase */
    @Bean public ListTicketUseCase listTicketUseCase(ITicketRepository r, vn.com.be_crm.application.shared.lookup.INameResolver n) { return new ListTicketUseCase(r, n); }

    // ===== Workflow / CSAT =====

    /** @return TicketWorkflowUseCase — luồng assign/start/resolve/approve/reject/receive/inspect/complete/close/reopen */
    @Bean public TicketWorkflowUseCase ticketWorkflowUseCase(ITicketRepository r, LogTicketEventUseCase log, CreateNotificationUseCase nuc) {
        return new TicketWorkflowUseCase(r, log, nuc);
    }
    /** @return SubmitCsatUseCase */
    @Bean public SubmitCsatUseCase submitCsatUseCase(ITicketRepository r) { return new SubmitCsatUseCase(r); }

    // ===== Return items =====

    /** @return CreateTicketReturnItemUseCase */
    @Bean public CreateTicketReturnItemUseCase createTicketReturnItemUseCase(ITicketReturnItemRepository r) { return new CreateTicketReturnItemUseCase(r); }
    /** @return UpdateTicketReturnItemUseCase */
    @Bean public UpdateTicketReturnItemUseCase updateTicketReturnItemUseCase(ITicketReturnItemRepository r) { return new UpdateTicketReturnItemUseCase(r); }
    /** @return DeleteTicketReturnItemUseCase */
    @Bean public DeleteTicketReturnItemUseCase deleteTicketReturnItemUseCase(ITicketReturnItemRepository r) { return new DeleteTicketReturnItemUseCase(r); }
    /** @return ListTicketReturnItemUseCase */
    @Bean public ListTicketReturnItemUseCase listTicketReturnItemUseCase(ITicketReturnItemRepository r) { return new ListTicketReturnItemUseCase(r); }

    // ===== Comments =====

    /** @return CreateTicketCommentUseCase */
    @Bean public CreateTicketCommentUseCase createTicketCommentUseCase(ITicketCommentRepository r) { return new CreateTicketCommentUseCase(r); }
    /** @return ListTicketCommentUseCase */
    @Bean public ListTicketCommentUseCase listTicketCommentUseCase(ITicketCommentRepository r, vn.com.be_crm.application.shared.lookup.INameResolver n) { return new ListTicketCommentUseCase(r, n); }

    // ===== SLA =====

    /** @return ListSlaPolicyUseCase */
    @Bean public ListSlaPolicyUseCase listSlaPolicyUseCase(ISlaPolicyRepository r) { return new ListSlaPolicyUseCase(r); }

    // ===== Trash & Handover =====

    /** @return ListDeletedTicketsUseCase */
    @Bean public ListDeletedTicketsUseCase listDeletedTicketsUseCase(ITicketRepository r) { return new ListDeletedTicketsUseCase(r); }
    /** @return RestoreTicketUseCase */
    @Bean public RestoreTicketUseCase restoreTicketUseCase(ITicketRepository r) { return new RestoreTicketUseCase(r); }
    /** @return PurgeTicketUseCase */
    @Bean public PurgeTicketUseCase purgeTicketUseCase(ITicketRepository r) { return new PurgeTicketUseCase(r); }
    /** @return HandoverBulkTicketUseCase */
    @Bean public HandoverBulkTicketUseCase handoverBulkTicketUseCase(ITicketRepository r) { return new HandoverBulkTicketUseCase(r); }
}
