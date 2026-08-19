package vn.com.be_crm.presentation.handover;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.handover.HandoverAllUseCase;
import vn.com.be_crm.core.dto.handover.HandoverAllCommand;
import vn.com.be_crm.core.error.frontend.ForbiddenException;
import vn.com.be_crm.core.util.SecurityUtils;
import vn.com.be_crm.core.response.ApiResponse;
import vn.com.be_crm.core.dto.handover.HandoverAllRequest;

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
     * ADMIN/SALES_MANAGER chọn tự do cặp fromUserId/toUserId bất kỳ; nhân viên thường chỉ được
     * bàn giao công việc của chính mình (fromUserId phải trùng userId đang đăng nhập).
     *
     * @param body JSON body chứa fromUserId, toUserId, reason
     * @param req  HTTP request (lấy userId hiện tại)
     * @return 200 OK
     */
    @PostMapping("/all")
    public ResponseEntity<ApiResponse<Void>> handoverAll(@Valid @RequestBody HandoverAllRequest body,
                                                           HttpServletRequest req) {
        Long currentUserId = (Long) req.getAttribute("userId");
        boolean isAdminOrManager = SecurityUtils.isAdminOrManager(SecurityContextHolder.getContext().getAuthentication());
        if (!isAdminOrManager && !currentUserId.equals(body.getFromUserId())) {
            throw new ForbiddenException("Bạn chỉ có thể bàn giao công việc của chính mình");
        }
        handoverAllUC.execute(HandoverAllCommand.builder()
                .fromUserId(body.getFromUserId())
                .toUserId(body.getToUserId())
                .reason(body.getReason()).build());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
