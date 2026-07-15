package vn.com.be_crm.infrastructure.shared.config.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.be_crm.application.invoice.command.*;
import vn.com.be_crm.application.invoice.query.*;
import vn.com.be_crm.domain.invoice.repository.IInvoiceItemRepository;
import vn.com.be_crm.domain.invoice.repository.IInvoicePaymentScheduleRepository;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRepository;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRevenueRecordRepository;

/**
 * Wire các UseCase của module Hóa đơn (invoice, item, revenue, payment, trash, handover, import, workflow) qua @Bean.
 */
@Configuration
public class InvoiceBeanConfig {

    // ===== Invoice =====

    /** @return CreateInvoiceUseCase */
    @Bean public CreateInvoiceUseCase createInvoiceUseCase(IInvoiceRepository r) { return new CreateInvoiceUseCase(r); }
    /** @return UpdateInvoiceUseCase */
    @Bean public UpdateInvoiceUseCase updateInvoiceUseCase(IInvoiceRepository r, RecomputeInvoiceTotalsUseCase rc) { return new UpdateInvoiceUseCase(r, rc); }
    /** @return DeleteInvoiceUseCase */
    @Bean public DeleteInvoiceUseCase deleteInvoiceUseCase(IInvoiceRepository r) { return new DeleteInvoiceUseCase(r); }
    /** @return GetInvoiceUseCase */
    @Bean public GetInvoiceUseCase getInvoiceUseCase(IInvoiceRepository r, vn.com.be_crm.application.shared.lookup.INameResolver n) { return new GetInvoiceUseCase(r, n); }
    /** @return ListInvoiceUseCase */
    @Bean public ListInvoiceUseCase listInvoiceUseCase(IInvoiceRepository r, vn.com.be_crm.application.shared.lookup.INameResolver n) { return new ListInvoiceUseCase(r, n); }
    /** @return InvoiceWorkflowUseCase — luồng trạng thái + suy ra trạng thái thanh toán */
    @Bean public InvoiceWorkflowUseCase invoiceWorkflowUseCase(IInvoiceRepository r, IInvoicePaymentScheduleRepository sr) { return new InvoiceWorkflowUseCase(r, sr); }

    // ===== Invoice Item =====

    /** @return CreateInvoiceItemUseCase */
    @Bean public CreateInvoiceItemUseCase createInvoiceItemUseCase(IInvoiceItemRepository r, RecomputeInvoiceTotalsUseCase rc) { return new CreateInvoiceItemUseCase(r, rc); }
    /** @return UpdateInvoiceItemUseCase */
    @Bean public UpdateInvoiceItemUseCase updateInvoiceItemUseCase(IInvoiceItemRepository r, RecomputeInvoiceTotalsUseCase rc) { return new UpdateInvoiceItemUseCase(r, rc); }
    /** @return DeleteInvoiceItemUseCase */
    @Bean public DeleteInvoiceItemUseCase deleteInvoiceItemUseCase(IInvoiceItemRepository r, RecomputeInvoiceTotalsUseCase rc) { return new DeleteInvoiceItemUseCase(r, rc); }
    /** @return ListInvoiceItemUseCase */
    @Bean public ListInvoiceItemUseCase listInvoiceItemUseCase(IInvoiceItemRepository r) { return new ListInvoiceItemUseCase(r); }

    // ===== Invoice Revenue Record =====

    /** @return CreateInvoiceRevenueRecordUseCase */
    @Bean public CreateInvoiceRevenueRecordUseCase createInvoiceRevenueRecordUseCase(IInvoiceRevenueRecordRepository r) { return new CreateInvoiceRevenueRecordUseCase(r); }
    /** @return UpdateInvoiceRevenueRecordUseCase */
    @Bean public UpdateInvoiceRevenueRecordUseCase updateInvoiceRevenueRecordUseCase(IInvoiceRevenueRecordRepository r) { return new UpdateInvoiceRevenueRecordUseCase(r); }
    /** @return DeleteInvoiceRevenueRecordUseCase */
    @Bean public DeleteInvoiceRevenueRecordUseCase deleteInvoiceRevenueRecordUseCase(IInvoiceRevenueRecordRepository r) { return new DeleteInvoiceRevenueRecordUseCase(r); }
    /** @return ListInvoiceRevenueRecordUseCase */
    @Bean public ListInvoiceRevenueRecordUseCase listInvoiceRevenueRecordUseCase(IInvoiceRevenueRecordRepository r) { return new ListInvoiceRevenueRecordUseCase(r); }

    // ===== Invoice Payment Schedule =====

    /** @return CreateInvoicePaymentScheduleUseCase */
    @Bean public CreateInvoicePaymentScheduleUseCase createInvoicePaymentScheduleUseCase(IInvoicePaymentScheduleRepository r) { return new CreateInvoicePaymentScheduleUseCase(r); }
    /** @return UpdateInvoicePaymentScheduleUseCase */
    @Bean public UpdateInvoicePaymentScheduleUseCase updateInvoicePaymentScheduleUseCase(IInvoicePaymentScheduleRepository r) { return new UpdateInvoicePaymentScheduleUseCase(r); }
    /** @return DeleteInvoicePaymentScheduleUseCase */
    @Bean public DeleteInvoicePaymentScheduleUseCase deleteInvoicePaymentScheduleUseCase(IInvoicePaymentScheduleRepository r) { return new DeleteInvoicePaymentScheduleUseCase(r); }
    /** @return ListInvoicePaymentScheduleUseCase */
    @Bean public ListInvoicePaymentScheduleUseCase listInvoicePaymentScheduleUseCase(IInvoicePaymentScheduleRepository r) { return new ListInvoicePaymentScheduleUseCase(r); }

    // ===== Trash =====

    /** @return ListDeletedInvoicesUseCase */
    @Bean public ListDeletedInvoicesUseCase listDeletedInvoicesUseCase(IInvoiceRepository r) { return new ListDeletedInvoicesUseCase(r); }
    /** @return RestoreInvoiceUseCase */
    @Bean public RestoreInvoiceUseCase restoreInvoiceUseCase(IInvoiceRepository r) { return new RestoreInvoiceUseCase(r); }
    /** @return PurgeInvoiceUseCase */
    @Bean public PurgeInvoiceUseCase purgeInvoiceUseCase(IInvoiceRepository r) { return new PurgeInvoiceUseCase(r); }

    // ===== Handover & Import =====

    /** @return HandoverBulkInvoiceUseCase */
    @Bean public HandoverBulkInvoiceUseCase handoverBulkInvoiceUseCase(IInvoiceRepository r) { return new HandoverBulkInvoiceUseCase(r); }
    /** @return ImportBulkInvoiceUseCase */
    @Bean public ImportBulkInvoiceUseCase importBulkInvoiceUseCase(IInvoiceRepository r) { return new ImportBulkInvoiceUseCase(r); }

    /** @return RecomputeInvoiceTotalsUseCase — tính lại tổng tiền từ dòng hàng (server là nguồn chân lý) */
    @Bean public RecomputeInvoiceTotalsUseCase recomputeInvoiceTotalsUseCase(IInvoiceRepository r, IInvoiceItemRepository ir) {
        return new RecomputeInvoiceTotalsUseCase(r, ir);
    }
}
