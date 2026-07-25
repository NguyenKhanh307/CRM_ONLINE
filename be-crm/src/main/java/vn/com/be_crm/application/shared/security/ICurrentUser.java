package vn.com.be_crm.application.shared.security;

/**
 * Port đọc ID người dùng của request hiện tại, để tầng application biết "ai đang thao tác"
 * mà không phải luồn userId qua từng command/use case.
 *
 * <p>Request công khai (web tracking) không có người dùng → trả null.
 */
public interface ICurrentUser {

    /** @return ID người dùng của request hiện tại, hoặc null nếu request không có JWT */
    Long id();
}
