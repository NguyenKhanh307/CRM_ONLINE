package vn.com.be_crm.application.shared.security;

import java.util.List;

/**
 * Port tạo access token — application layer không biết chi tiết JWT.
 */
public interface ITokenProvider {

    /**
     * Tạo access token cho người dùng đã xác thực.
     *
     * @param userId              ID người dùng
     * @param email               email (subject)
     * @param roles               danh sách code vai trò
     * @param dataAccessFromYear  năm sớm nhất được xem data (null = không giới hạn)
     * @return token dạng chuỗi
     */
    String generateToken(Long userId, String email, List<String> roles, Integer dataAccessFromYear);
}
