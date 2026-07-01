package vn.com.be_crm.presentation.service.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Body khi nhân viên nhập ghi chú (note) cho phiếu. */
@Getter @Setter @NoArgsConstructor
public class CreateTicketCommentRequest {
    /** Nội dung ghi chú. */
    @NotBlank @Size(max = 1000) private String content;
    /** True = nội bộ, false = gửi khách (mặc định true). */
    private Boolean isInternal;
}
