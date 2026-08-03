package vn.com.be_crm.core.error.frontend;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vn.com.be_crm.core.response.ApiResponse;

import java.util.stream.Collectors;

// bắt lỗi nghiệp vụ có message an toàn để trả thẳng ra frontend
@RestControllerAdvice
public class FrontendErrorHandler {

    // tiềm năng/khách hàng/... không tìm thấy -> 404
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage(), 404));
    }

    // từ chối quyền ở mức bản ghi (vd xem khách hàng của người khác) -> 403
    // đặt trước handler DomainException vì ForbiddenException kế thừa DomainException
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse<Void>> handleForbidden(ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ex.getMessage(), 403));
    }

    // lỗi nghiệp vụ chung -> 400
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiResponse<Void>> handleDomain(DomainException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage(), 400));
    }

    // lỗi validation Bean Validation (@Valid) -> 422 kèm danh sách lỗi field
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiResponse.error(message, 422));
    }

    // từ chối quyền truy cập (@PreAuthorize ném từ controller, bay vào @RestControllerAdvice
    // trước ExceptionTranslationFilter — thiếu handler riêng sẽ rơi vào lỗi hệ thống và trả 500 sai) -> 403
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(org.springframework.security.access.AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("Bạn không có quyền thực hiện thao tác này", 403));
    }
}
