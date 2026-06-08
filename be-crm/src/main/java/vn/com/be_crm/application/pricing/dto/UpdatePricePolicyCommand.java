package vn.com.be_crm.application.pricing.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.pricing.enums.PricePolicyStatus;

import java.time.LocalDate;

/** Input DTO khi cập nhật chính sách giá. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdatePricePolicyCommand {
    private Long id;
    @Size(max = 40) private String name;
    @Size(max = 20) private String type;
    private Integer priority;
    private LocalDate startDate;
    private LocalDate endDate;
    private PricePolicyStatus status;
}
