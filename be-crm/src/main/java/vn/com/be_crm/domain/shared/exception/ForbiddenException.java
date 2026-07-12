package vn.com.be_crm.domain.shared.exception;

/**
 * Ném khi người dùng không đủ quyền xem/thao tác trên một bản ghi cụ thể (record-level).
 * Khác với @PreAuthorize (kiểm tra ở mức endpoint) — dùng cho kiểm tra owner của từng bản ghi.
 */
public class ForbiddenException extends DomainException {

    /**
     * @param message mô tả lý do từ chối
     */
    public ForbiddenException(String message) {
        super(message);
    }
}
