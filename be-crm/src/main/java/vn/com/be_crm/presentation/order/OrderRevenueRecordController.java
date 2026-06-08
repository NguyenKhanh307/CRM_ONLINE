package vn.com.be_crm.presentation.order;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.order.command.*;
import vn.com.be_crm.application.order.dto.*;
import vn.com.be_crm.application.order.query.ListOrderRevenueRecordUseCase;
import vn.com.be_crm.presentation.shared.ApiResponse;

import java.util.List;

/**
 * REST controller cho bản ghi doanh thu đơn hàng.
 */
@RestController
@RequestMapping("/api/orders/{orderId}/revenue-records")
public class OrderRevenueRecordController {
    private final CreateOrderRevenueRecordUseCase createUC;
    private final UpdateOrderRevenueRecordUseCase updateUC;
    private final DeleteOrderRevenueRecordUseCase deleteUC;
    private final ListOrderRevenueRecordUseCase listUC;

    /** @param createUC tạo mới @param updateUC cập nhật @param deleteUC xóa @param listUC danh sách */
    public OrderRevenueRecordController(CreateOrderRevenueRecordUseCase createUC, UpdateOrderRevenueRecordUseCase updateUC,
                                         DeleteOrderRevenueRecordUseCase deleteUC, ListOrderRevenueRecordUseCase listUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC; this.listUC = listUC;
    }

    /** Tạo mới bản ghi doanh thu. @param orderId ID @param cmd body @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<OrderRevenueRecordResult>> create(@PathVariable Long orderId,
                                                                         @Valid @RequestBody CreateOrderRevenueRecordCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(
                CreateOrderRevenueRecordCommand.builder().orderId(orderId).userId(cmd.getUserId())
                        .revenueAmount(cmd.getRevenueAmount()).percentage(cmd.getPercentage()).note(cmd.getNote()).build())));
    }

    /** Lấy danh sách bản ghi doanh thu. @param orderId ID @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderRevenueRecordResult>>> list(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.ok(listUC.execute(orderId)));
    }

    /** Cập nhật bản ghi doanh thu. @param id ID @param cmd body @return 200 */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderRevenueRecordResult>> update(@PathVariable Long orderId, @PathVariable Long id,
                                                                         @Valid @RequestBody UpdateOrderRevenueRecordCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateOrderRevenueRecordCommand.builder().id(id).revenueAmount(cmd.getRevenueAmount())
                        .percentage(cmd.getPercentage()).note(cmd.getNote()).build())));
    }

    /** Xóa bản ghi doanh thu. @param id ID @return 204 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long orderId, @PathVariable Long id) {
        deleteUC.execute(id); return ResponseEntity.noContent().build();
    }
}
