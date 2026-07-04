package vn.com.be_crm.domain.campaign.enums;

import vn.com.be_crm.domain.shared.exception.DomainException;

import java.util.Map;
import java.util.Set;

/**
 * Trạng thái chiến dịch theo vòng đời triển khai:
 * draft → scheduled → running → (paused ↔ running) → completed; có thể cancelled trước khi hoàn tất.
 */
public enum CampaignStatus {
    draft, scheduled, running, paused, completed, cancelled;

    /** Bảng các bước chuyển hợp lệ giữa các trạng thái. */
    private static final Map<CampaignStatus, Set<CampaignStatus>> ALLOWED = Map.of(
            draft, Set.of(scheduled, running, cancelled),
            scheduled, Set.of(running, cancelled),
            running, Set.of(paused, completed, cancelled),
            paused, Set.of(running, completed, cancelled),
            completed, Set.of(),
            cancelled, Set.of()
    );

    /**
     * Đảm bảo bước chuyển từ trạng thái hiện tại sang target là hợp lệ, nếu không ném DomainException.
     * @param target trạng thái đích
     */
    public void ensureCanTransitionTo(CampaignStatus target) {
        if (!ALLOWED.getOrDefault(this, Set.of()).contains(target)) {
            throw new DomainException("Không thể chuyển chiến dịch từ '" + this + "' sang '" + target + "'");
        }
    }
}
