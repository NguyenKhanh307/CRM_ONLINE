package vn.com.be_crm.application.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Input DTO khi nhân viên nhập ghi chú (note) cho phiếu. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateTicketCommentCommand {
    /** ID phiếu — controller set từ path. */
    private Long ticketId;
    @NotBlank(message = "Nội dung không được để trống") @Size(max = 1000) private String content;
    /** True = nội bộ, false = gửi khách. Mặc định true nếu null. */
    private Boolean isInternal;
    /** ID người viết — controller set từ JWT. */
    private Long authorId;
}
