package vn.com.be_crm.presentation.shared;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vn.com.be_crm.domain.shared.exception.DomainException;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

import java.util.stream.Collectors;

/**
 * Bắt và chuẩn hóa lỗi toàn hệ thống thành ApiResponse.
 * Lỗi hệ thống/SQL chỉ log ở server, KHÔNG trả chi tiết ra client (tránh lộ cấu trúc DB).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Xử lý khi entity không tìm thấy — trả 404.
     *
     * @param ex NotFoundException từ domain
     * @return 404 Not Found
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage(), 404));
    }

    /**
     * Xử lý lỗi nghiệp vụ chung — trả 400.
     *
     * @param ex DomainException từ domain
     * @return 400 Bad Request
     */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiResponse<Void>> handleDomain(DomainException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage(), 400));
    }

    /**
     * Xử lý lỗi validation Bean Validation (@Valid) — trả 422.
     *
     * @param ex MethodArgumentNotValidException từ Spring
     * @return 422 Unprocessable Entity với danh sách lỗi field
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiResponse.error(message, 422));
    }

    /**
     * Xử lý vi phạm ràng buộc DB (trùng mã/unique, FK...) — trả 400 với thông báo thân thiện.
     * Chi tiết SQL chỉ ghi log server, không trả ra client.
     *
     * @param ex ConstraintViolationException từ Hibernate khi commit
     * @return 400 Bad Request
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("Vi phạm ràng buộc DB (constraint: {}): {}", ex.getConstraintName(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        "Dữ liệu bị trùng (mã hoặc giá trị duy nhất đã tồn tại), vui lòng kiểm tra lại.", 400));
    }

    /**
     * Fallback cho mọi exception chưa được xử lý — trả 500 với thông báo chung.
     * Stack trace đầy đủ ghi vào log server.
     *
     * @param ex exception bất kỳ
     * @return 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex) {
        log.error("Lỗi hệ thống chưa được xử lý", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau.", 500));
    }
}
