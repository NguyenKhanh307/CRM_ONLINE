package vn.com.be_crm.core.error.system;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vn.com.be_crm.core.response.ApiResponse;

// bắt lỗi hệ thống/hạ tầng — chỉ log ở server, KHÔNG lộ chi tiết SQL/stack trace ra client
@RestControllerAdvice
public class SystemErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(SystemErrorHandler.class);

    // vi phạm ràng buộc DB (trùng mã/unique, FK...) -> 400 với thông báo thân thiện
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("Vi phạm ràng buộc DB (constraint: {}): {}", ex.getConstraintName(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        "Dữ liệu bị trùng (mã hoặc giá trị duy nhất đã tồn tại), vui lòng kiểm tra lại.", 400));
    }

    // fallback cho mọi exception chưa được xử lý -> 500 với thông báo chung, stack trace đầy đủ ghi vào log server
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex) {
        log.error("Lỗi hệ thống chưa được xử lý", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau.", 500));
    }
}
