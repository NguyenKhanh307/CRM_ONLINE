package vn.com.be_crm.application.quotation.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.quotation.enums.QuotationStatus;

import java.time.LocalDate;
import java.util.List;

// input khi tạo mới báo giá (kèm danh sách dòng hàng nếu có)
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateQuotationCommand {
    @NotBlank(message = "Mã báo giá không được để trống") @Size(max = 20) private String code;
    private Long customerId;
    private Long contactId;
    private Long opportunityId;
    private Long pricePolicyId;
    private Long ownerId;
    @FutureOrPresent(message = "Ngày báo giá không được là ngày quá khứ") private LocalDate quoteDate;
    @FutureOrPresent(message = "Ngày hiệu lực không được là ngày quá khứ") private LocalDate validUntil;
    private QuotationStatus status;
    @Size(max = 255) private String note;
    // dòng hàng tạo kèm báo giá (quotationId bỏ trống)
    @Valid private List<CreateQuotationItemCommand> items;
}
