package vn.com.be_crm.application.opportunity.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Lệnh đổi giai đoạn pipeline của một cơ hội (dùng cho kéo-thả trên bảng Kanban).
 * Trạng thái (open/won/lost) KHÔNG nhận từ client — luôn suy ra từ giai đoạn.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeOpportunityStageCommand {

    /** ID cơ hội — lấy từ path, KHÔNG đặt @NotNull (validate chạy trước khi controller gán). */
    private Long id;

    /** Giai đoạn đích. */
    @NotNull(message = "Giai đoạn không được để trống")
    private Long stageId;

    /** Lý do thắng/thua — bắt buộc về nghiệp vụ khi kéo vào cột thua (FE hỏi qua ReasonModal). */
    private String winLossReason;
}
