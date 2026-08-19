package vn.com.be_crm.core.config.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.be_crm.core.notify.NotifyAssignmentUseCase;
import vn.com.be_crm.application.order.command.*;
import vn.com.be_crm.application.order.query.*;
import vn.com.be_crm.domain.order.repository.IOrderItemRepository;
import vn.com.be_crm.domain.order.repository.IOrderRepository;
import vn.com.be_crm.core.tx.port.ITransactionRunner;

// wire các UseCase của module Đơn hàng (order, item, trash, handover, import, workflow, xuất hóa đơn)
@Configuration
public class OrderBeanConfig {

    // ===== Order =====

    @Bean public CreateOrderUseCase createOrderUseCase(IOrderRepository r,
            vn.com.be_crm.application.lead.command.NotifyLeadFirstOrderUseCase notifyLeadFirstOrderUC) {
        return new CreateOrderUseCase(r, notifyLeadFirstOrderUC);
    }
    @Bean public UpdateOrderUseCase updateOrderUseCase(IOrderRepository r, IOrderItemRepository ir, NotifyAssignmentUseCase n) { return new UpdateOrderUseCase(r, ir, n); }
    @Bean public DeleteOrderUseCase deleteOrderUseCase(IOrderRepository r) { return new DeleteOrderUseCase(r); }
    @Bean public GetOrderUseCase getOrderUseCase(IOrderRepository r, IOrderItemRepository ir, vn.com.be_crm.core.lookup.port.INameResolver n) { return new GetOrderUseCase(r, ir, n); }
    @Bean public ListOrderUseCase listOrderUseCase(IOrderRepository r, IOrderItemRepository ir, vn.com.be_crm.core.lookup.port.INameResolver n) { return new ListOrderUseCase(r, ir, n); }
    // luồng trạng thái đơn hàng
    @Bean public OrderWorkflowUseCase orderWorkflowUseCase(IOrderRepository r) { return new OrderWorkflowUseCase(r); }
    // đánh dấu đơn hàng đã xuất hóa đơn (khóa + hoàn tất) — Hóa đơn được tạo riêng qua AddPage
    @Bean public MarkOrderConvertedUseCase markOrderConvertedUseCase(IOrderRepository r, ITransactionRunner tx) {
        return new MarkOrderConvertedUseCase(r, tx);
    }

    // ===== Order Item =====

    @Bean public CreateOrderItemUseCase createOrderItemUseCase(IOrderItemRepository r) { return new CreateOrderItemUseCase(r); }
    @Bean public UpdateOrderItemUseCase updateOrderItemUseCase(IOrderItemRepository r) { return new UpdateOrderItemUseCase(r); }
    @Bean public DeleteOrderItemUseCase deleteOrderItemUseCase(IOrderItemRepository r) { return new DeleteOrderItemUseCase(r); }
    @Bean public ListOrderItemUseCase listOrderItemUseCase(IOrderItemRepository r) { return new ListOrderItemUseCase(r); }

    // ===== Trash =====

    @Bean public ListDeletedOrdersUseCase listDeletedOrdersUseCase(IOrderRepository r) { return new ListDeletedOrdersUseCase(r); }
    @Bean public RestoreOrderUseCase restoreOrderUseCase(IOrderRepository r) { return new RestoreOrderUseCase(r); }
    @Bean public PurgeOrderUseCase purgeOrderUseCase(IOrderRepository r) { return new PurgeOrderUseCase(r); }

    // ===== Handover & Import =====

    @Bean public HandoverBulkOrderUseCase handoverBulkOrderUseCase(IOrderRepository r, NotifyAssignmentUseCase n) { return new HandoverBulkOrderUseCase(r, n); }
    @Bean public ImportBulkOrderUseCase importBulkOrderUseCase(IOrderRepository r, ITransactionRunner tx) { return new ImportBulkOrderUseCase(r, tx); }
}
