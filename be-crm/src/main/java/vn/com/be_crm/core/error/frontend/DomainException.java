package vn.com.be_crm.core.error.frontend;

/**
 * Base exception cho tất cả lỗi nghiệp vụ trong domain layer.
 */
public class DomainException extends RuntimeException {

    /**
     * @param message mô tả lỗi nghiệp vụ
     */
    public DomainException(String message) {
        super(message);
    }

    /**
     * @param message mô tả lỗi nghiệp vụ
     * @param cause   nguyên nhân gốc
     */
    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
