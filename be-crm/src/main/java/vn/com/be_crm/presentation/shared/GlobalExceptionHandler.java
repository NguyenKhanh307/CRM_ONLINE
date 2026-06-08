package vn.com.be_crm.presentation.shared;

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
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

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
     * Fallback cho mọi exception chưa được xử lý — trả 500.
     *
     * @param ex exception bất kỳ
     * @return 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Lỗi hệ thống: " + ex.getMessage(), 500));
    }
}
