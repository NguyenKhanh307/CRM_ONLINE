package vn.com.be_crm.presentation.notification;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.notification.command.MarkNotificationReadUseCase;
import vn.com.be_crm.application.notification.dto.NotificationResult;
import vn.com.be_crm.application.notification.query.CountUnreadNotificationsUseCase;
import vn.com.be_crm.application.notification.query.ListMyNotificationsUseCase;
import vn.com.be_crm.presentation.shared.ApiResponse;

import java.util.List;

/**
 * REST controller cho thông báo của người dùng hiện tại.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final ListMyNotificationsUseCase listUC;
    private final CountUnreadNotificationsUseCase countUC;
    private final MarkNotificationReadUseCase markUC;

    /** @param listUC danh sách @param countUC đếm chưa đọc @param markUC đánh dấu đã đọc */
    public NotificationController(ListMyNotificationsUseCase listUC, CountUnreadNotificationsUseCase countUC,
                                  MarkNotificationReadUseCase markUC) {
        this.listUC = listUC; this.countUC = countUC; this.markUC = markUC;
    }

    /** Lấy danh sách thông báo của tôi. @param req HTTP request @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResult>>> list(HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        return ResponseEntity.ok(ApiResponse.ok(listUC.execute(userId)));
    }

    /** Đếm số thông báo chưa đọc. @param req HTTP request @return 200 */
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> unreadCount(HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        return ResponseEntity.ok(ApiResponse.ok(countUC.execute(userId)));
    }

    /** Đánh dấu một thông báo đã đọc. @param id ID @param req HTTP request @return 200 */
    @PostMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> read(@PathVariable Long id, HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        markUC.markOne(id, userId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    /** Đánh dấu tất cả thông báo đã đọc. @param req HTTP request @return 200 */
    @PostMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> readAll(HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        markUC.markAll(userId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
