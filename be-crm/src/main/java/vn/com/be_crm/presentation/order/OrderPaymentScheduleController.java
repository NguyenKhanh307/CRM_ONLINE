package vn.com.be_crm.presentation.order;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.order.command.*;
import vn.com.be_crm.application.order.dto.*;
import vn.com.be_crm.application.order.query.ListOrderPaymentScheduleUseCase;
import vn.com.be_crm.presentation.shared.ApiResponse;

import java.util.List;

/**
 * REST controller cho đợt thanh toán đơn hàng.
 */
@RestController
@RequestMapping("/api/orders/{orderId}/payment-schedules")
public class OrderPaymentScheduleController {
    private final CreateOrderPaymentScheduleUseCase createUC;
    private final UpdateOrderPaymentScheduleUseCase updateUC;
    private final DeleteOrderPaymentScheduleUseCase deleteUC;
    private final ListOrderPaymentScheduleUseCase listUC;

    /** @param createUC tạo mới @param updateUC cập nhật @param deleteUC xóa @param listUC danh sách */
    public OrderPaymentScheduleController(CreateOrderPaymentScheduleUseCase createUC, UpdateOrderPaymentScheduleUseCase updateUC,
                                           DeleteOrderPaymentScheduleUseCase deleteUC, ListOrderPaymentScheduleUseCase listUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC; this.listUC = listUC;
    }

    /** Tạo mới đợt thanh toán. @param orderId ID @param cmd body @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<OrderPaymentScheduleResult>> create(@PathVariable Long orderId,
                                                                           @Valid @RequestBody CreateOrderPaymentScheduleCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(
                CreateOrderPaymentScheduleCommand.builder().orderId(orderId).installmentNo(cmd.getInstallmentNo())
                        .dueDate(cmd.getDueDate()).amount(cmd.getAmount()).paidAmount(cmd.getPaidAmount())
                        .status(cmd.getStatus()).paidAt(cmd.getPaidAt()).note(cmd.getNote()).build())));
    }

    /** Lấy danh sách đợt thanh toán. @param orderId ID @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderPaymentScheduleResult>>> list(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.ok(listUC.execute(orderId)));
    }

    /** Cập nhật đợt thanh toán. @param id ID @param cmd body @return 200 */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderPaymentScheduleResult>> update(@PathVariable Long orderId, @PathVariable Long id,
                                                                           @Valid @RequestBody UpdateOrderPaymentScheduleCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateOrderPaymentScheduleCommand.builder().id(id).dueDate(cmd.getDueDate())
                        .amount(cmd.getAmount()).paidAmount(cmd.getPaidAmount()).status(cmd.getStatus())
                        .paidAt(cmd.getPaidAt()).note(cmd.getNote()).build())));
    }

    /** Xóa đợt thanh toán. @param id ID @return 204 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long orderId, @PathVariable Long id) {
        deleteUC.execute(id); return ResponseEntity.noContent().build();
    }
}
