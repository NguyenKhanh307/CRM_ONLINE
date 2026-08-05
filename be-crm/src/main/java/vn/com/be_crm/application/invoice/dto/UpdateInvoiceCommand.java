package vn.com.be_crm.application.invoice.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// input khi cập nhật hóa đơn
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateInvoiceCommand {
    private Long id;
    private Long orderId;
    private Long ownerId;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    @Size(max = 255) private String note;
}
