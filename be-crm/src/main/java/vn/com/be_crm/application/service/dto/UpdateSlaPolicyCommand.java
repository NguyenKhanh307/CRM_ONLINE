package vn.com.be_crm.application.service.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.service.enums.TicketPriority;

/** Input DTO khi cập nhật chính sách SLA — không có `code` (không đổi được sau khi tạo). */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateSlaPolicyCommand {
    private Long id;
    @Size(max = 50) private String name;
    private TicketPriority priority;
    @Positive(message = "Hạn phản hồi đầu tiên phải lớn hơn 0") private Integer firstResponseHours;
    @Positive(message = "Hạn giải quyết phải lớn hơn 0") private Integer resolutionHours;
    private Boolean isActive;
}
