package vn.com.be_crm.application.service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.service.enums.ReturnReason;
import vn.com.be_crm.domain.service.enums.TicketChannel;
import vn.com.be_crm.domain.service.enums.TicketPriority;
import vn.com.be_crm.domain.service.enums.TicketType;

import java.util.List;

// input khi tạo mới phiếu hỗ trợ (kèm dòng hàng trả/đổi nếu có)
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateTicketCommand {
    @NotBlank(message = "Mã phiếu không được để trống") @Size(max = 20) private String code;
    private TicketType type;
    @NotBlank(message = "Tiêu đề không được để trống") @Size(max = 150) private String subject;
    @Size(max = 1000) private String description;
    private Long orderId;
    private TicketChannel channel;
    private TicketPriority priority;
    private ReturnReason reason;
    private Long assignedUserId;
    // dòng hàng trả/đổi tạo kèm phiếu (ticketId bỏ trống)
    @Valid private List<CreateTicketReturnItemCommand> returnItems;
}
