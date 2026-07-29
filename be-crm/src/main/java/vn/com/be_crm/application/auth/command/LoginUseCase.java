package vn.com.be_crm.application.auth.command;

import vn.com.be_crm.application.auth.dto.LoginResult;
import vn.com.be_crm.application.shared.security.IPasswordEncoder;
import vn.com.be_crm.application.shared.security.ITokenProvider;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.auth.entity.User;
import vn.com.be_crm.domain.auth.repository.IPermissionRepository;
import vn.com.be_crm.domain.auth.repository.IUserRepository;
import vn.com.be_crm.domain.auth.repository.IUserRoleRepository;
import vn.com.be_crm.domain.shared.exception.DomainException;

import java.util.List;

/**
 * Use case đăng nhập: xác thực thông tin và cấp JWT token.
 */
public class LoginUseCase implements IUseCase<LoginCommand, LoginResult> {

    private final IUserRepository userRepository;
    private final IUserRoleRepository userRoleRepository;
    private final IPermissionRepository permissionRepository;
    private final IPasswordEncoder passwordEncoder;
    private final ITokenProvider tokenProvider;

    /**
     * @param userRepository       repository người dùng
     * @param userRoleRepository   repository gán vai trò
     * @param permissionRepository repository quyền hạn
     * @param passwordEncoder      port kiểm tra mật khẩu
     * @param tokenProvider        port tạo token
     */
    public LoginUseCase(IUserRepository userRepository,
                        IUserRoleRepository userRoleRepository,
                        IPermissionRepository permissionRepository,
                        IPasswordEncoder passwordEncoder,
                        ITokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.permissionRepository = permissionRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    /**
     * Xác thực email/password, trả về JWT và thông tin người dùng.
     *
     * @param command LoginCommand chứa email và password
     * @return LoginResult với token và thông tin cơ bản
     * @throws DomainException nếu thông tin đăng nhập không hợp lệ
     */
    @Override
    public LoginResult execute(LoginCommand command) {
        User user = userRepository.findByEmail(command.getEmail())
                .orElseThrow(() -> new DomainException("Email không tồn tại trong hệ thống"));

        if (user.getStatus() != vn.com.be_crm.domain.auth.enums.UserStatus.active) {
            throw new DomainException("Tài khoản chưa được kích hoạt");
        }

        if (!passwordEncoder.matches(command.getPassword(), user.getPasswordHash())) {
            throw new DomainException("Mật khẩu không đúng");
        }

        List<String> roles = userRoleRepository.findRoleCodesByUserId(user.getId());
        List<String> permissions = permissionRepository.findCodesByUserId(user.getId());
        String token = tokenProvider.generateToken(user.getId(), user.getEmail(), roles, permissions, user.getDataAccessFromYear());

        return new LoginResult(token, user.getId(), user.getEmail(), user.getFullName(), user.getAvatarUrl(), roles, permissions);
    }
}
