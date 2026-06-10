package vn.com.be_crm.application.auth.command;

import vn.com.be_crm.application.auth.dto.LoginResult;
import vn.com.be_crm.application.shared.security.IPasswordEncoder;
import vn.com.be_crm.application.shared.security.ITokenProvider;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.auth.entity.User;
import vn.com.be_crm.domain.auth.repository.IUserRepository;
import vn.com.be_crm.domain.auth.repository.IUserRoleRepository;

import java.util.List;

/**
 * Use case đăng nhập: xác thực thông tin và cấp JWT token.
 */
public class LoginUseCase implements IUseCase<LoginCommand, LoginResult> {

    private final IUserRepository userRepository;
    private final IUserRoleRepository userRoleRepository;
    private final IPasswordEncoder passwordEncoder;
    private final ITokenProvider tokenProvider;

    /**
     * @param userRepository     repository người dùng
     * @param userRoleRepository repository gán vai trò
     * @param passwordEncoder    port kiểm tra mật khẩu
     * @param tokenProvider      port tạo token
     */
    public LoginUseCase(IUserRepository userRepository,
                        IUserRoleRepository userRoleRepository,
                        IPasswordEncoder passwordEncoder,
                        ITokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    /**
     * Xác thực email/password, trả về JWT và thông tin người dùng.
     *
     * @param command LoginCommand chứa email và password
     * @return LoginResult với token và thông tin cơ bản
     * @throws RuntimeException nếu thông tin đăng nhập không hợp lệ
     */
    @Override
    public LoginResult execute(LoginCommand command) {
        if (!command.getEmail().toLowerCase().endsWith("@gmail.com")) {
            throw new RuntimeException("Chỉ chấp nhận địa chỉ @gmail.com");
        }

        User user = userRepository.findByEmail(command.getEmail())
                .orElseThrow(() -> new RuntimeException("Email hoặc mật khẩu không đúng"));

        if (user.getStatus() != vn.com.be_crm.domain.auth.enums.UserStatus.active) {
            throw new RuntimeException("Tài khoản chưa được kích hoạt");
        }

        if (!passwordEncoder.matches(command.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Email hoặc mật khẩu không đúng");
        }

        List<String> roles = userRoleRepository.findRoleCodesByUserId(user.getId());
        String token = tokenProvider.generateToken(user.getId(), user.getEmail(), roles, user.getDataAccessFromYear());

        return new LoginResult(token, user.getId(), user.getEmail(), user.getFullName(), roles);
    }
}
