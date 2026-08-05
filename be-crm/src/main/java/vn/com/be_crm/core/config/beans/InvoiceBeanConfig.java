package vn.com.be_crm.core.config.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.be_crm.application.invoice.command.*;
import vn.com.be_crm.application.invoice.query.*;
import vn.com.be_crm.domain.invoice.repository.IInvoiceItemRepository;
import vn.com.be_crm.domain.invoice.repository.IInvoicePaymentScheduleRepository;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRepository;

// wire các UseCase của module Hóa đơn (invoice, item, payment, trash, handover, import, workflow)
@Configuration
public class InvoiceBeanConfig {

    // ===== Invoice =====

    @Bean public CreateInvoiceUseCase createInvoiceUseCase(IInvoiceRepository r) { return new CreateInvoiceUseCase(r); }
    @Bean public UpdateInvoiceUseCase updateInvoiceUseCase(IInvoiceRepository r, IInvoiceItemRepository ir) { return new UpdateInvoiceUseCase(r, ir); }
    @Bean public DeleteInvoiceUseCase deleteInvoiceUseCase(IInvoiceRepository r) { return new DeleteInvoiceUseCase(r); }
    @Bean public GetInvoiceUseCase getInvoiceUseCase(IInvoiceRepository r, IInvoiceItemRepository ir, vn.com.be_crm.core.lookup.port.INameResolver n) { return new GetInvoiceUseCase(r, ir, n); }
    @Bean public ListInvoiceUseCase listInvoiceUseCase(IInvoiceRepository r, IInvoiceItemRepository ir, vn.com.be_crm.core.lookup.port.INameResolver n) { return new ListInvoiceUseCase(r, ir, n); }
    // luồng trạng thái + suy ra trạng thái thanh toán
    @Bean public InvoiceWorkflowUseCase invoiceWorkflowUseCase(IInvoiceRepository r, IInvoiceItemRepository ir, IInvoicePaymentScheduleRepository sr) { return new InvoiceWorkflowUseCase(r, ir, sr); }

    // ===== Invoice Item =====

    @Bean public CreateInvoiceItemUseCase createInvoiceItemUseCase(IInvoiceItemRepository r) { return new CreateInvoiceItemUseCase(r); }
    @Bean public UpdateInvoiceItemUseCase updateInvoiceItemUseCase(IInvoiceItemRepository r) { return new UpdateInvoiceItemUseCase(r); }
    @Bean public DeleteInvoiceItemUseCase deleteInvoiceItemUseCase(IInvoiceItemRepository r) { return new DeleteInvoiceItemUseCase(r); }
    @Bean public ListInvoiceItemUseCase listInvoiceItemUseCase(IInvoiceItemRepository r) { return new ListInvoiceItemUseCase(r); }

    // ===== Invoice Payment Schedule =====

    @Bean public CreateInvoicePaymentScheduleUseCase createInvoicePaymentScheduleUseCase(IInvoicePaymentScheduleRepository r, IInvoiceRepository ir, IInvoiceItemRepository iir) { return new CreateInvoicePaymentScheduleUseCase(r, ir, iir); }
    @Bean public UpdateInvoicePaymentScheduleUseCase updateInvoicePaymentScheduleUseCase(IInvoicePaymentScheduleRepository r, IInvoiceRepository ir, IInvoiceItemRepository iir) { return new UpdateInvoicePaymentScheduleUseCase(r, ir, iir); }
    @Bean public DeleteInvoicePaymentScheduleUseCase deleteInvoicePaymentScheduleUseCase(IInvoicePaymentScheduleRepository r) { return new DeleteInvoicePaymentScheduleUseCase(r); }
    @Bean public ListInvoicePaymentScheduleUseCase listInvoicePaymentScheduleUseCase(IInvoicePaymentScheduleRepository r) { return new ListInvoicePaymentScheduleUseCase(r); }

    // ===== Trash =====

    @Bean public ListDeletedInvoicesUseCase listDeletedInvoicesUseCase(IInvoiceRepository r) { return new ListDeletedInvoicesUseCase(r); }
    @Bean public RestoreInvoiceUseCase restoreInvoiceUseCase(IInvoiceRepository r) { return new RestoreInvoiceUseCase(r); }
    @Bean public PurgeInvoiceUseCase purgeInvoiceUseCase(IInvoiceRepository r) { return new PurgeInvoiceUseCase(r); }

    // ===== Handover & Import =====

    @Bean public HandoverBulkInvoiceUseCase handoverBulkInvoiceUseCase(IInvoiceRepository r) { return new HandoverBulkInvoiceUseCase(r); }
    @Bean public ImportBulkInvoiceUseCase importBulkInvoiceUseCase(IInvoiceRepository r) { return new ImportBulkInvoiceUseCase(r); }
}
