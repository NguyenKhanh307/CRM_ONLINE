package vn.com.be_crm.presentation.order;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.order.command.*;
import vn.com.be_crm.application.order.dto.*;
import vn.com.be_crm.application.order.query.*;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.presentation.shared.ApiResponse;
import vn.com.be_crm.presentation.shared.PageResponse;

/**
 * REST controller cho nghiệp vụ quản lý đơn hàng.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final CreateOrderUseCase createUC;
    private final UpdateOrderUseCase updateUC;
    private final DeleteOrderUseCase deleteUC;
    private final GetOrderUseCase getUC;
    private final ListOrderUseCase listUC;

    /** @param createUC tạo mới @param updateUC cập nhật @param deleteUC xóa @param getUC lấy @param listUC danh sách */
    public OrderController(CreateOrderUseCase createUC, UpdateOrderUseCase updateUC, DeleteOrderUseCase deleteUC,
                            GetOrderUseCase getUC, ListOrderUseCase listUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC;
        this.getUC = getUC; this.listUC = listUC;
    }

    /** Tạo mới đơn hàng. @param cmd JSON body @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResult>> create(@Valid @RequestBody CreateOrderCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(cmd)));
    }

    /** Lấy danh sách đơn hàng. @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OrderResult>>> list(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(listUC.execute(
                PageRequest.builder().page(page).size(size).sortBy(sortBy).sortDir(sortDir).build()))));
    }

    /** Lấy đơn hàng theo ID. @param id ID @return 200 */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResult>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(getUC.execute(id)));
    }

    /** Cập nhật đơn hàng. @param id ID @param cmd body @return 200 */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResult>> update(@PathVariable Long id, @Valid @RequestBody UpdateOrderCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateOrderCommand.builder().id(id).customerId(cmd.getCustomerId()).contactId(cmd.getContactId())
                        .ownerId(cmd.getOwnerId()).executorUnitId(cmd.getExecutorUnitId()).warehouseId(cmd.getWarehouseId())
                        .orderDate(cmd.getOrderDate()).status(cmd.getStatus()).paymentStatus(cmd.getPaymentStatus())
                        .subtotal(cmd.getSubtotal()).discount(cmd.getDiscount()).tax(cmd.getTax())
                        .total(cmd.getTotal()).note(cmd.getNote()).build())));
    }

    /** Xóa mềm đơn hàng. @param id ID @return 204 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUC.execute(id); return ResponseEntity.noContent().build();
    }
}
