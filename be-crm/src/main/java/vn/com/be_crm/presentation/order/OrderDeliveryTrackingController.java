package vn.com.be_crm.presentation.order;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.order.command.*;
import vn.com.be_crm.application.order.dto.*;
import vn.com.be_crm.application.order.query.ListOrderDeliveryTrackingUseCase;
import vn.com.be_crm.presentation.shared.ApiResponse;

import java.util.List;

/**
 * REST controller cho theo dõi giao hàng đơn hàng.
 */
@RestController
@RequestMapping("/api/orders/{orderId}/delivery-tracking")
public class OrderDeliveryTrackingController {
    private final CreateOrderDeliveryTrackingUseCase createUC;
    private final UpdateOrderDeliveryTrackingUseCase updateUC;
    private final DeleteOrderDeliveryTrackingUseCase deleteUC;
    private final ListOrderDeliveryTrackingUseCase listUC;

    /** @param createUC tạo mới @param updateUC cập nhật @param deleteUC xóa @param listUC danh sách */
    public OrderDeliveryTrackingController(CreateOrderDeliveryTrackingUseCase createUC, UpdateOrderDeliveryTrackingUseCase updateUC,
                                            DeleteOrderDeliveryTrackingUseCase deleteUC, ListOrderDeliveryTrackingUseCase listUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC; this.listUC = listUC;
    }

    /** Tạo mới theo dõi giao hàng. @param orderId ID @param cmd body @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<OrderDeliveryTrackingResult>> create(@PathVariable Long orderId,
                                                                            @Valid @RequestBody CreateOrderDeliveryTrackingCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(
                CreateOrderDeliveryTrackingCommand.builder().orderId(orderId).status(cmd.getStatus())
                        .carrier(cmd.getCarrier()).trackingNo(cmd.getTrackingNo())
                        .shippedAt(cmd.getShippedAt()).deliveredAt(cmd.getDeliveredAt()).note(cmd.getNote()).build())));
    }

    /** Lấy danh sách theo dõi giao hàng. @param orderId ID @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderDeliveryTrackingResult>>> list(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.ok(listUC.execute(orderId)));
    }

    /** Cập nhật theo dõi giao hàng. @param id ID @param cmd body @return 200 */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDeliveryTrackingResult>> update(@PathVariable Long orderId, @PathVariable Long id,
                                                                            @Valid @RequestBody UpdateOrderDeliveryTrackingCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateOrderDeliveryTrackingCommand.builder().id(id).status(cmd.getStatus())
                        .carrier(cmd.getCarrier()).trackingNo(cmd.getTrackingNo())
                        .shippedAt(cmd.getShippedAt()).deliveredAt(cmd.getDeliveredAt()).note(cmd.getNote()).build())));
    }

    /** Xóa theo dõi giao hàng. @param id ID @return 204 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long orderId, @PathVariable Long id) {
        deleteUC.execute(id); return ResponseEntity.noContent().build();
    }
}
