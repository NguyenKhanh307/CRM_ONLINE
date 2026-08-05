package vn.com.be_crm.application.quotation.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.quotation.enums.QuotationStatus;

import java.time.LocalDate;

// input khi cập nhật báo giá
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateQuotationCommand {
    private Long id;
    private Long customerId;
    private Long contactId;
    private Long opportunityId;
    private Long pricePolicyId;
    private Long ownerId;
    private LocalDate quoteDate;
    private LocalDate validUntil;
    private QuotationStatus status;
    @Size(max = 255) private String note;
}
