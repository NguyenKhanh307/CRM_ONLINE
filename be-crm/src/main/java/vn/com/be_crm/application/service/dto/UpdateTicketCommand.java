package vn.com.be_crm.application.service.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.service.enums.ReturnReason;
import vn.com.be_crm.domain.service.enums.TicketChannel;
import vn.com.be_crm.domain.service.enums.TicketPriority;
import vn.com.be_crm.domain.service.enums.TicketType;

// input khi cập nhật phiếu (KHÔNG nhận status — đổi qua hành động)
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateTicketCommand {
    // ID phiếu — controller set từ path; KHÔNG đặt @NotNull
    private Long id;
    private TicketType type;
    @Size(max = 150) private String subject;
    @Size(max = 1000) private String description;
    private Long orderId;
    private TicketChannel channel;
    private TicketPriority priority;
    private ReturnReason reason;
    private Long assignedUserId;
}
