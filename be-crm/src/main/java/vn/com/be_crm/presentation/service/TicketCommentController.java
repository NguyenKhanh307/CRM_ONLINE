package vn.com.be_crm.presentation.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.service.command.CreateTicketCommentUseCase;
import vn.com.be_crm.application.service.dto.CreateTicketCommentCommand;
import vn.com.be_crm.application.service.dto.TicketCommentResult;
import vn.com.be_crm.application.service.query.ListTicketCommentUseCase;
import vn.com.be_crm.presentation.service.request.CreateTicketCommentRequest;
import vn.com.be_crm.core.response.ApiResponse;

import java.util.List;

/**
 * REST controller cho ghi chú / lịch sử phiếu.
 */
@RestController
@RequestMapping("/api/tickets/{ticketId}/comments")
public class TicketCommentController {
    private final CreateTicketCommentUseCase createUC;
    private final ListTicketCommentUseCase listUC;

    /** @param createUC tạo ghi chú @param listUC danh sách ghi chú */
    public TicketCommentController(CreateTicketCommentUseCase createUC, ListTicketCommentUseCase listUC) {
        this.createUC = createUC; this.listUC = listUC;
    }

    /** Lấy danh sách ghi chú / lịch sử của phiếu. @param ticketId ID phiếu @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TicketCommentResult>>> list(@PathVariable Long ticketId) {
        return ResponseEntity.ok(ApiResponse.ok(listUC.execute(ticketId)));
    }

    /** Thêm ghi chú (note) do người dùng nhập. @param ticketId ID phiếu @param body nội dung @param req HTTP request @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<TicketCommentResult>> create(@PathVariable Long ticketId,
            @Valid @RequestBody CreateTicketCommentRequest body, HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(
                CreateTicketCommentCommand.builder().ticketId(ticketId).content(body.getContent())
                        .isInternal(body.getIsInternal()).authorId(userId).build())));
    }
}
