package vn.com.be_crm.application.auth.command;

import vn.com.be_crm.application.auth.dto.LoginResult;
import vn.com.be_crm.application.shared.security.IGoogleTokenVerifier;
import vn.com.be_crm.application.shared.security.IGoogleTokenVerifier.GoogleUserInfo;
import vn.com.be_crm.application.shared.security.ITokenProvider;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.auth.entity.User;
import vn.com.be_crm.domain.auth.enums.UserStatus;
import vn.com.be_crm.domain.auth.repository.IPermissionRepository;
import vn.com.be_crm.domain.auth.repository.IUserRepository;
import vn.com.be_crm.domain.auth.repository.IUserRoleRepository;
import vn.com.be_crm.domain.shared.exception.DomainException;

import java.util.List;

/**
 * Use case đăng nhập bằng Google: xác thực ID token, kiểm tra email có trong hệ thống,
 * tự điền ảnh đại diện từ Google (khi trống) rồi cấp JWT của hệ thống.
 */
public class GoogleLoginUseCase implements IUseCase<GoogleLoginCommand, LoginResult> {

    private final IGoogleTokenVerifier googleTokenVerifier;
    private final IUserRepository userRepository;
    private final IUserRoleRepository userRoleRepository;
    private final IPermissionRepository permissionRepository;
    private final ITokenProvider tokenProvider;

    /**
     * @param googleTokenVerifier  port xác thực Google ID token
     * @param userRepository       repository người dùng
     * @param userRoleRepository   repository gán vai trò
     * @param permissionRepository repository quyền hạn
     * @param tokenProvider        port tạo JWT
     */
    public GoogleLoginUseCase(IGoogleTokenVerifier googleTokenVerifier,
                              IUserRepository userRepository,
                              IUserRoleRepository userRoleRepository,
                              IPermissionRepository permissionRepository,
                              ITokenProvider tokenProvider) {
        this.googleTokenVerifier = googleTokenVerifier;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.permissionRepository = permissionRepository;
        this.tokenProvider = tokenProvider;
    }

    /**
     * Xác thực token Google, kiểm tra email tồn tại + active, cấp JWT và trả thông tin đăng nhập.
     *
     * @param command chứa Google ID token
     * @return LoginResult với token và thông tin cơ bản
     * @throws DomainException nếu token sai, email không có trong hệ thống hoặc tài khoản chưa kích hoạt
     */
    @Override
    public LoginResult execute(GoogleLoginCommand command) {
        GoogleUserInfo info = googleTokenVerifier.verify(command.idToken());

        User user = userRepository.findByEmail(info.email())
                .orElseThrow(() -> new DomainException("Tài khoản chưa được cấp quyền truy cập"));

        if (user.getStatus() != UserStatus.active) {
            throw new DomainException("Tài khoản chưa được kích hoạt");
        }

        // Chỉ điền ảnh đại diện từ Google khi user chưa có ảnh (không ghi đè ảnh tự đặt).
        if ((user.getAvatarUrl() == null || user.getAvatarUrl().isBlank())
                && info.picture() != null && !info.picture().isBlank()) {
            user = userRepository.save(user.toBuilder().avatarUrl(info.picture()).build());
        }

        List<String> roles = userRoleRepository.findRoleCodesByUserId(user.getId());
        List<String> permissions = permissionRepository.findCodesByUserId(user.getId());
        String token = tokenProvider.generateToken(user.getId(), user.getEmail(), roles, permissions, user.getDataAccessFromYear());

        return new LoginResult(token, user.getId(), user.getEmail(), user.getFullName(), roles, permissions);
    }
}
