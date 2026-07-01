package vn.com.be_crm.presentation.service;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.service.command.CreateTicketReturnItemUseCase;
import vn.com.be_crm.application.service.command.DeleteTicketReturnItemUseCase;
import vn.com.be_crm.application.service.command.UpdateTicketReturnItemUseCase;
import vn.com.be_crm.application.service.dto.CreateTicketReturnItemCommand;
import vn.com.be_crm.application.service.dto.TicketReturnItemResult;
import vn.com.be_crm.application.service.dto.UpdateTicketReturnItemCommand;
import vn.com.be_crm.application.service.query.ListTicketReturnItemUseCase;
import vn.com.be_crm.presentation.shared.ApiResponse;

import java.util.List;

/**
 * REST controller cho dòng hàng trả/đổi trong phiếu.
 */
@RestController
@RequestMapping("/api/tickets/{ticketId}/return-items")
public class TicketReturnItemController {
    private final CreateTicketReturnItemUseCase createUC;
    private final UpdateTicketReturnItemUseCase updateUC;
    private final DeleteTicketReturnItemUseCase deleteUC;
    private final ListTicketReturnItemUseCase listUC;

    /** @param createUC tạo @param updateUC cập nhật @param deleteUC xóa @param listUC danh sách */
    public TicketReturnItemController(CreateTicketReturnItemUseCase createUC, UpdateTicketReturnItemUseCase updateUC,
                                      DeleteTicketReturnItemUseCase deleteUC, ListTicketReturnItemUseCase listUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC; this.listUC = listUC;
    }

    /** Tạo mới dòng hàng trả/đổi. @param ticketId ID phiếu @param cmd body @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<TicketReturnItemResult>> create(@PathVariable Long ticketId,
                                                                      @Valid @RequestBody CreateTicketReturnItemCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(
                CreateTicketReturnItemCommand.builder().ticketId(ticketId).invoiceItemId(cmd.getInvoiceItemId())
                        .productId(cmd.getProductId()).quantity(cmd.getQuantity()).unitPrice(cmd.getUnitPrice())
                        .amount(cmd.getAmount()).reason(cmd.getReason()).conditionNote(cmd.getConditionNote()).build())));
    }

    /** Lấy danh sách dòng hàng trả/đổi. @param ticketId ID phiếu @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TicketReturnItemResult>>> list(@PathVariable Long ticketId) {
        return ResponseEntity.ok(ApiResponse.ok(listUC.execute(ticketId)));
    }

    /** Cập nhật dòng hàng trả/đổi. @param id ID @param cmd body @return 200 */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketReturnItemResult>> update(@PathVariable Long ticketId, @PathVariable Long id,
                                                                      @Valid @RequestBody UpdateTicketReturnItemCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateTicketReturnItemCommand.builder().id(id).invoiceItemId(cmd.getInvoiceItemId())
                        .productId(cmd.getProductId()).quantity(cmd.getQuantity()).unitPrice(cmd.getUnitPrice())
                        .amount(cmd.getAmount()).reason(cmd.getReason()).conditionNote(cmd.getConditionNote()).build())));
    }

    /** Xóa dòng hàng trả/đổi. @param id ID @return 204 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long ticketId, @PathVariable Long id) {
        deleteUC.execute(id);
        return ResponseEntity.noContent().build();
    }
}
