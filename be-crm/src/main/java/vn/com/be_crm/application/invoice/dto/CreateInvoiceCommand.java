package vn.com.be_crm.application.invoice.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

// input khi tạo mới hóa đơn (kèm dòng hàng nếu có)
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateInvoiceCommand {
    @NotBlank(message = "Mã hóa đơn không được để trống") @Size(max = 20) private String code;
    private Long orderId;
    private Long ownerId;
    @FutureOrPresent(message = "Ngày hóa đơn không được là ngày quá khứ") private LocalDate invoiceDate;
    @FutureOrPresent(message = "Hạn thanh toán không được là ngày quá khứ") private LocalDate dueDate;
    @Size(max = 255) private String note;
    // dòng hàng tạo kèm hóa đơn (invoiceId bỏ trống)
    @Valid private List<CreateInvoiceItemCommand> items;
}
