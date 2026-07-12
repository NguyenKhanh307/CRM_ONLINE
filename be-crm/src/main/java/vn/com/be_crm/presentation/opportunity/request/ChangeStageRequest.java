package vn.com.be_crm.presentation.opportunity.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/** HTTP body cho hành động đổi giai đoạn cơ hội (kéo-thả trên bảng Kanban). */
@Getter
@Setter
public class ChangeStageRequest {

    /** Giai đoạn đích. */
    @NotNull(message = "Giai đoạn không được để trống")
    private Long stageId;

    /** Lý do thắng/thua (FE hỏi qua ReasonModal khi kéo vào cột thắng/thua). */
    private String winLossReason;
}
