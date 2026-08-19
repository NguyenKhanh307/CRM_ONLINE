package vn.com.be_crm.application.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.service.enums.TicketPriority;

/** Input DTO khi tạo mới chính sách SLA. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateSlaPolicyCommand {
    @NotBlank(message = "Mã chính sách không được để trống") @Size(max = 20) private String code;
    @NotBlank(message = "Tên chính sách không được để trống") @Size(max = 50) private String name;
    @NotNull(message = "Độ ưu tiên không được để trống") private TicketPriority priority;
    @NotNull @Positive(message = "Hạn phản hồi đầu tiên phải lớn hơn 0") private Integer firstResponseHours;
    @NotNull @Positive(message = "Hạn giải quyết phải lớn hơn 0") private Integer resolutionHours;
    private Boolean isActive;
}
