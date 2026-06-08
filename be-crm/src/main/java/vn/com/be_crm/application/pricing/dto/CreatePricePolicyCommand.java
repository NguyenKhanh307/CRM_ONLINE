package vn.com.be_crm.application.pricing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.pricing.enums.PricePolicyStatus;

import java.time.LocalDate;

/** Input DTO khi tạo mới chính sách giá. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreatePricePolicyCommand {
    @NotBlank(message = "Mã chính sách không được để trống") @Size(max = 20) private String code;
    @NotBlank(message = "Tên chính sách không được để trống") @Size(max = 40) private String name;
    @Size(max = 20) private String type;
    private Integer priority;
    private LocalDate startDate;
    private LocalDate endDate;
    private PricePolicyStatus status;
    private Long createdBy;
}
