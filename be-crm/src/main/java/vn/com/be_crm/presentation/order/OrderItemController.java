package vn.com.be_crm.presentation.order;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.order.command.*;
import vn.com.be_crm.application.order.dto.*;
import vn.com.be_crm.application.order.query.ListOrderItemUseCase;
import vn.com.be_crm.core.response.ApiResponse;

import java.util.List;

// REST controller cho dòng sản phẩm trong đơn hàng
@RestController
@RequestMapping("/api/orders/{orderId}/items")
public class OrderItemController {
    private final CreateOrderItemUseCase createUC;
    private final UpdateOrderItemUseCase updateUC;
    private final DeleteOrderItemUseCase deleteUC;
    private final ListOrderItemUseCase listUC;

    public OrderItemController(CreateOrderItemUseCase createUC, UpdateOrderItemUseCase updateUC,
                              DeleteOrderItemUseCase deleteUC, ListOrderItemUseCase listUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC; this.listUC = listUC;
    }

    // tạo mới dòng đơn hàng
    @PostMapping
    public ResponseEntity<ApiResponse<OrderItemResult>> create(@PathVariable Long orderId,
                                                              @Valid @RequestBody CreateOrderItemCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(
                CreateOrderItemCommand.builder().orderId(orderId).productId(cmd.getProductId())
                        .unit(cmd.getUnit())
                        .quantity(cmd.getQuantity()).unitPrice(cmd.getUnitPrice())
                        .discount(cmd.getDiscount()).taxRate(cmd.getTaxRate())
                        .note(cmd.getNote()).build())));
    }

    // lấy danh sách dòng đơn hàng
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderItemResult>>> list(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.ok(listUC.execute(orderId)));
    }

    // cập nhật dòng đơn hàng
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderItemResult>> update(@PathVariable Long orderId, @PathVariable Long id,
                                                              @Valid @RequestBody UpdateOrderItemCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateOrderItemCommand.builder().id(id).productId(cmd.getProductId())
                        .quantity(cmd.getQuantity()).unitPrice(cmd.getUnitPrice())
                        .discount(cmd.getDiscount()).taxRate(cmd.getTaxRate())
                        .note(cmd.getNote()).build())));
    }

    // xóa dòng đơn hàng
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long orderId, @PathVariable Long id) {
        deleteUC.execute(id); return ResponseEntity.noContent().build();
    }
}
