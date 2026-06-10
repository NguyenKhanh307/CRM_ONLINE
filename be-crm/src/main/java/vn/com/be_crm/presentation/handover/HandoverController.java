package vn.com.be_crm.presentation.handover;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.handover.HandoverAllUseCase;
import vn.com.be_crm.application.shared.dto.HandoverAllCommand;
import vn.com.be_crm.infrastructure.shared.util.SecurityUtils;
import vn.com.be_crm.presentation.shared.ApiResponse;
import vn.com.be_crm.presentation.shared.HandoverAllRequest;

/**
 * REST controller cho nghiệp vụ bàn giao toàn bộ công việc.
 */
@RestController
@RequestMapping("/api/handover")
public class HandoverController {

    private final HandoverAllUseCase handoverAllUC;

    /** @param handoverAllUC use case bàn giao toàn bộ */
    public HandoverController(HandoverAllUseCase handoverAllUC) {
        this.handoverAllUC = handoverAllUC;
    }

    /**
     * Bàn giao toàn bộ công việc từ một user sang user khác.
     * Chỉ ADMIN hoặc SALES_MANAGER được phép gọi.
     *
     * @param body JSON body chứa fromUserId, toUserId, reason
     * @return 200 OK
     */
    @PostMapping("/all")
    public ResponseEntity<ApiResponse<Void>> handoverAll(@Valid @RequestBody HandoverAllRequest body) {
        boolean isAdminOrManager = SecurityUtils.isAdminOrManager(SecurityContextHolder.getContext().getAuthentication());
        if (!isAdminOrManager) {
            return ResponseEntity.status(403).body(ApiResponse.error("Chỉ Admin hoặc Quản lý mới có quyền bàn giao toàn bộ", 403));
        }
        handoverAllUC.execute(HandoverAllCommand.builder()
                .fromUserId(body.getFromUserId())
                .toUserId(body.getToUserId())
                .reason(body.getReason()).build());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
