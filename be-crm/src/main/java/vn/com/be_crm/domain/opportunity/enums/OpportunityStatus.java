package vn.com.be_crm.domain.opportunity.enums;

import vn.com.be_crm.domain.opportunity.entity.OpportunityStage;

/** Trạng thái cơ hội bán hàng. Suy ra tự động từ giai đoạn pipeline (không sửa tay). */
public enum OpportunityStatus {
    open, won, lost;

    /**
     * Suy ra trạng thái cơ hội từ giai đoạn pipeline được chọn.
     * Giai đoạn thắng → won, giai đoạn thua → lost, còn lại (kể cả chưa chọn) → open.
     * @param stage giai đoạn hiện tại (có thể null nếu cơ hội chưa gắn giai đoạn)
     * @return trạng thái suy ra
     */
    public static OpportunityStatus fromStage(OpportunityStage stage) {
        if (stage == null) return open;
        if (Boolean.TRUE.equals(stage.getIsWon())) return won;
        if (Boolean.TRUE.equals(stage.getIsLost())) return lost;
        return open;
    }
}
