package vn.com.be_crm.presentation.copilot;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.com.be_crm.application.copilot.dto.AskCopilotQuery;
import vn.com.be_crm.application.copilot.dto.CopilotAnswer;
import vn.com.be_crm.application.copilot.query.AskCopilotUseCase;
import vn.com.be_crm.core.util.SecurityUtils;
import vn.com.be_crm.presentation.copilot.request.AskCopilotRequest;
import vn.com.be_crm.core.response.ApiResponse;

/**
 * REST controller cho trợ lý AI Copilot — hỏi đáp dữ liệu CRM bằng ngôn ngữ tự nhiên.
 */
@RestController
@RequestMapping("/api/copilot")
public class CopilotController {

    private final AskCopilotUseCase askUC;

    /** @param askUC use case hỏi trợ lý AI */
    public CopilotController(AskCopilotUseCase askUC) {
        this.askUC = askUC;
    }

    /**
     * Hỏi trợ lý AI. Dữ liệu được giới hạn theo quyền: ADMIN/SALES_MANAGER xem toàn bộ,
     * nhân viên chỉ xem bản ghi mình phụ trách.
     *
     * @param body câu hỏi
     * @param req  HTTP request (lấy userId từ JWT)
     * @return câu trả lời của trợ lý
     */
    @PostMapping("/ask")
    public ResponseEntity<ApiResponse<CopilotAnswer>> ask(@Valid @RequestBody AskCopilotRequest body,
                                                          HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isPrivileged = SecurityUtils.isAdminOrManager(auth);
        Long ownerId = isPrivileged ? null : userId;
        CopilotAnswer answer = askUC.execute(new AskCopilotQuery(ownerId, isPrivileged, body.question()));
        return ResponseEntity.ok(ApiResponse.ok(answer));
    }
}
