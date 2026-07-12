package vn.com.be_crm.presentation.duplicate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.com.be_crm.application.duplicate.dto.DuplicateMatch;
import vn.com.be_crm.application.duplicate.query.CheckDuplicateUseCase;
import vn.com.be_crm.presentation.shared.ApiResponse;

import java.util.List;

/**
 * REST controller dò trùng email/SĐT/MST — dùng cho cảnh báo khi nhập liệu và khi convert tiềm năng.
 */
@RestController
@RequestMapping("/api/duplicates")
public class DuplicateController {

    private final CheckDuplicateUseCase checkUC;

    /** @param checkUC use case dò trùng */
    public DuplicateController(CheckDuplicateUseCase checkUC) {
        this.checkUC = checkUC;
    }

    /**
     * Dò bản ghi trùng trong tiềm năng / khách hàng / liên hệ.
     *
     * @param email         email cần dò
     * @param phone         số điện thoại cần dò
     * @param taxCode       mã số thuế cần dò
     * @param excludeModule phân hệ của bản ghi đang sửa (bỏ chính nó khỏi kết quả)
     * @param excludeId     ID bản ghi đang sửa
     * @return 200 kèm danh sách bản ghi nghi trùng (rỗng nếu không có)
     */
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<List<DuplicateMatch>>> check(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String taxCode,
            @RequestParam(required = false) String excludeModule,
            @RequestParam(required = false) Long excludeId) {
        return ResponseEntity.ok(ApiResponse.ok(checkUC.execute(email, phone, taxCode, excludeModule, excludeId)));
    }
}
