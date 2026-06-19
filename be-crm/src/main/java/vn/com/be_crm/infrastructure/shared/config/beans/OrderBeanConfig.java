package vn.com.be_crm.infrastructure.shared.config.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.be_crm.application.order.command.*;
import vn.com.be_crm.application.order.query.*;
import vn.com.be_crm.domain.order.repository.IOrderDeliveryTrackingRepository;
import vn.com.be_crm.domain.order.repository.IOrderItemRepository;
import vn.com.be_crm.domain.order.repository.IOrderPaymentScheduleRepository;
import vn.com.be_crm.domain.order.repository.IOrderRepository;
import vn.com.be_crm.domain.order.repository.IOrderRevenueRecordRepository;

/**
 * Wire các UseCase của module Order (order, item, revenue, payment, delivery, trash, handover, import) qua @Bean.
 */
@Configuration
public class OrderBeanConfig {

    // ===== Order =====

    /** @return CreateOrderUseCase */
    @Bean public CreateOrderUseCase createOrderUseCase(IOrderRepository r) { return new CreateOrderUseCase(r); }
    /** @return UpdateOrderUseCase */
    @Bean public UpdateOrderUseCase updateOrderUseCase(IOrderRepository r) { return new UpdateOrderUseCase(r); }
    /** @return DeleteOrderUseCase */
    @Bean public DeleteOrderUseCase deleteOrderUseCase(IOrderRepository r) { return new DeleteOrderUseCase(r); }
    /** @return GetOrderUseCase */
    @Bean public GetOrderUseCase getOrderUseCase(IOrderRepository r) { return new GetOrderUseCase(r); }
    /** @return ListOrderUseCase */
    @Bean public ListOrderUseCase listOrderUseCase(IOrderRepository r) { return new ListOrderUseCase(r); }

    // ===== Order Item =====

    /** @return CreateOrderItemUseCase */
    @Bean public CreateOrderItemUseCase createOrderItemUseCase(IOrderItemRepository r) { return new CreateOrderItemUseCase(r); }
    /** @return UpdateOrderItemUseCase */
    @Bean public UpdateOrderItemUseCase updateOrderItemUseCase(IOrderItemRepository r) { return new UpdateOrderItemUseCase(r); }
    /** @return DeleteOrderItemUseCase */
    @Bean public DeleteOrderItemUseCase deleteOrderItemUseCase(IOrderItemRepository r) { return new DeleteOrderItemUseCase(r); }
    /** @return ListOrderItemUseCase */
    @Bean public ListOrderItemUseCase listOrderItemUseCase(IOrderItemRepository r) { return new ListOrderItemUseCase(r); }

    // ===== Order Revenue Record =====

    /** @return CreateOrderRevenueRecordUseCase */
    @Bean public CreateOrderRevenueRecordUseCase createOrderRevenueRecordUseCase(IOrderRevenueRecordRepository r) { return new CreateOrderRevenueRecordUseCase(r); }
    /** @return UpdateOrderRevenueRecordUseCase */
    @Bean public UpdateOrderRevenueRecordUseCase updateOrderRevenueRecordUseCase(IOrderRevenueRecordRepository r) { return new UpdateOrderRevenueRecordUseCase(r); }
    /** @return DeleteOrderRevenueRecordUseCase */
    @Bean public DeleteOrderRevenueRecordUseCase deleteOrderRevenueRecordUseCase(IOrderRevenueRecordRepository r) { return new DeleteOrderRevenueRecordUseCase(r); }
    /** @return ListOrderRevenueRecordUseCase */
    @Bean public ListOrderRevenueRecordUseCase listOrderRevenueRecordUseCase(IOrderRevenueRecordRepository r) { return new ListOrderRevenueRecordUseCase(r); }

    // ===== Order Payment Schedule =====

    /** @return CreateOrderPaymentScheduleUseCase */
    @Bean public CreateOrderPaymentScheduleUseCase createOrderPaymentScheduleUseCase(IOrderPaymentScheduleRepository r) { return new CreateOrderPaymentScheduleUseCase(r); }
    /** @return UpdateOrderPaymentScheduleUseCase */
    @Bean public UpdateOrderPaymentScheduleUseCase updateOrderPaymentScheduleUseCase(IOrderPaymentScheduleRepository r) { return new UpdateOrderPaymentScheduleUseCase(r); }
    /** @return DeleteOrderPaymentScheduleUseCase */
    @Bean public DeleteOrderPaymentScheduleUseCase deleteOrderPaymentScheduleUseCase(IOrderPaymentScheduleRepository r) { return new DeleteOrderPaymentScheduleUseCase(r); }
    /** @return ListOrderPaymentScheduleUseCase */
    @Bean public ListOrderPaymentScheduleUseCase listOrderPaymentScheduleUseCase(IOrderPaymentScheduleRepository r) { return new ListOrderPaymentScheduleUseCase(r); }

    // ===== Order Delivery Tracking =====

    /** @return CreateOrderDeliveryTrackingUseCase */
    @Bean public CreateOrderDeliveryTrackingUseCase createOrderDeliveryTrackingUseCase(IOrderDeliveryTrackingRepository r) { return new CreateOrderDeliveryTrackingUseCase(r); }
    /** @return UpdateOrderDeliveryTrackingUseCase */
    @Bean public UpdateOrderDeliveryTrackingUseCase updateOrderDeliveryTrackingUseCase(IOrderDeliveryTrackingRepository r) { return new UpdateOrderDeliveryTrackingUseCase(r); }
    /** @return DeleteOrderDeliveryTrackingUseCase */
    @Bean public DeleteOrderDeliveryTrackingUseCase deleteOrderDeliveryTrackingUseCase(IOrderDeliveryTrackingRepository r) { return new DeleteOrderDeliveryTrackingUseCase(r); }
    /** @return ListOrderDeliveryTrackingUseCase */
    @Bean public ListOrderDeliveryTrackingUseCase listOrderDeliveryTrackingUseCase(IOrderDeliveryTrackingRepository r) { return new ListOrderDeliveryTrackingUseCase(r); }

    // ===== Trash =====

    /** @return ListDeletedOrdersUseCase */
    @Bean public ListDeletedOrdersUseCase listDeletedOrdersUseCase(IOrderRepository r) { return new ListDeletedOrdersUseCase(r); }
    /** @return RestoreOrderUseCase */
    @Bean public RestoreOrderUseCase restoreOrderUseCase(IOrderRepository r) { return new RestoreOrderUseCase(r); }
    /** @return PurgeOrderUseCase */
    @Bean public PurgeOrderUseCase purgeOrderUseCase(IOrderRepository r) { return new PurgeOrderUseCase(r); }

    // ===== Handover & Import =====

    /** @return HandoverBulkOrderUseCase */
    @Bean public HandoverBulkOrderUseCase handoverBulkOrderUseCase(IOrderRepository r) { return new HandoverBulkOrderUseCase(r); }
    /** @return ImportBulkOrderUseCase */
    @Bean public ImportBulkOrderUseCase importBulkOrderUseCase(IOrderRepository r) { return new ImportBulkOrderUseCase(r); }
}
