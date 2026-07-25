package vn.com.be_crm.infrastructure.shared.config.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.be_crm.application.shared.notify.NotifyAssignmentUseCase;
import vn.com.be_crm.application.order.command.*;
import vn.com.be_crm.application.order.query.*;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRepository;
import vn.com.be_crm.domain.order.repository.IOrderItemRepository;
import vn.com.be_crm.domain.order.repository.IOrderRepository;
import vn.com.be_crm.application.shared.tx.ITransactionRunner;

/**
 * Wire các UseCase của module Đơn hàng (order, item, trash, handover, import, workflow, xuất hóa đơn) qua @Bean.
 */
@Configuration
public class OrderBeanConfig {

    // ===== Order =====

    /** @return CreateOrderUseCase */
    @Bean public CreateOrderUseCase createOrderUseCase(IOrderRepository r) { return new CreateOrderUseCase(r); }
    /** @return UpdateOrderUseCase */
    @Bean public UpdateOrderUseCase updateOrderUseCase(IOrderRepository r, RecomputeOrderTotalsUseCase rc, NotifyAssignmentUseCase n) { return new UpdateOrderUseCase(r, rc, n); }
    /** @return DeleteOrderUseCase */
    @Bean public DeleteOrderUseCase deleteOrderUseCase(IOrderRepository r) { return new DeleteOrderUseCase(r); }
    /** @return GetOrderUseCase */
    @Bean public GetOrderUseCase getOrderUseCase(IOrderRepository r, vn.com.be_crm.application.shared.lookup.INameResolver n) { return new GetOrderUseCase(r, n); }
    /** @return ListOrderUseCase */
    @Bean public ListOrderUseCase listOrderUseCase(IOrderRepository r, vn.com.be_crm.application.shared.lookup.INameResolver n) { return new ListOrderUseCase(r, n); }
    /** @return OrderWorkflowUseCase — luồng trạng thái đơn hàng */
    @Bean public OrderWorkflowUseCase orderWorkflowUseCase(IOrderRepository r) { return new OrderWorkflowUseCase(r); }
    /** @return CreateInvoiceFromOrderUseCase — xuất hóa đơn 1-1 từ đơn hàng */
    @Bean public CreateInvoiceFromOrderUseCase createInvoiceFromOrderUseCase(IOrderRepository r, IOrderItemRepository ir, IInvoiceRepository invr,
                                                                             ITransactionRunner tx) {
        return new CreateInvoiceFromOrderUseCase(r, ir, invr, tx);
    }

    // ===== Order Item =====

    /** @return CreateOrderItemUseCase */
    @Bean public CreateOrderItemUseCase createOrderItemUseCase(IOrderItemRepository r, RecomputeOrderTotalsUseCase rc) { return new CreateOrderItemUseCase(r, rc); }
    /** @return UpdateOrderItemUseCase */
    @Bean public UpdateOrderItemUseCase updateOrderItemUseCase(IOrderItemRepository r, RecomputeOrderTotalsUseCase rc) { return new UpdateOrderItemUseCase(r, rc); }
    /** @return DeleteOrderItemUseCase */
    @Bean public DeleteOrderItemUseCase deleteOrderItemUseCase(IOrderItemRepository r, RecomputeOrderTotalsUseCase rc) { return new DeleteOrderItemUseCase(r, rc); }
    /** @return ListOrderItemUseCase */
    @Bean public ListOrderItemUseCase listOrderItemUseCase(IOrderItemRepository r) { return new ListOrderItemUseCase(r); }

    // ===== Trash =====

    /** @return ListDeletedOrdersUseCase */
    @Bean public ListDeletedOrdersUseCase listDeletedOrdersUseCase(IOrderRepository r) { return new ListDeletedOrdersUseCase(r); }
    /** @return RestoreOrderUseCase */
    @Bean public RestoreOrderUseCase restoreOrderUseCase(IOrderRepository r) { return new RestoreOrderUseCase(r); }
    /** @return PurgeOrderUseCase */
    @Bean public PurgeOrderUseCase purgeOrderUseCase(IOrderRepository r) { return new PurgeOrderUseCase(r); }

    // ===== Handover & Import =====

    /** @return HandoverBulkOrderUseCase */
    @Bean public HandoverBulkOrderUseCase handoverBulkOrderUseCase(IOrderRepository r, NotifyAssignmentUseCase n) { return new HandoverBulkOrderUseCase(r, n); }
    /** @return ImportBulkOrderUseCase */
    @Bean public ImportBulkOrderUseCase importBulkOrderUseCase(IOrderRepository r) { return new ImportBulkOrderUseCase(r); }

    /** @return RecomputeOrderTotalsUseCase — tính lại tổng tiền từ dòng hàng (server là nguồn chân lý) */
    @Bean public RecomputeOrderTotalsUseCase recomputeOrderTotalsUseCase(IOrderRepository r, IOrderItemRepository ir) {
        return new RecomputeOrderTotalsUseCase(r, ir);
    }
}
