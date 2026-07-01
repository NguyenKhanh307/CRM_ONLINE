package vn.com.be_crm.presentation.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.com.be_crm.application.service.dto.SlaPolicyResult;
import vn.com.be_crm.application.service.query.ListSlaPolicyUseCase;
import vn.com.be_crm.presentation.shared.ApiResponse;

import java.util.List;

/**
 * REST controller cho chính sách SLA.
 */
@RestController
@RequestMapping("/api/sla-policies")
public class SlaPolicyController {
    private final ListSlaPolicyUseCase listUC;

    /** @param listUC danh sách chính sách SLA */
    public SlaPolicyController(ListSlaPolicyUseCase listUC) { this.listUC = listUC; }

    /** Lấy toàn bộ chính sách SLA. @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<List<SlaPolicyResult>>> list() {
        return ResponseEntity.ok(ApiResponse.ok(listUC.execute()));
    }
}
