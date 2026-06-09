package vn.com.be_crm.application.auth.command;

import vn.com.be_crm.application.auth.dto.ActivateAccountCommand;
import vn.com.be_crm.application.auth.dto.UserResult;
import vn.com.be_crm.application.auth.mapper.UserCommandMapper;
import vn.com.be_crm.application.shared.security.IPasswordEncoder;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.auth.entity.User;
import vn.com.be_crm.domain.auth.enums.UserStatus;
import vn.com.be_crm.domain.auth.repository.IUserRepository;

import java.time.LocalDateTime;

/**
 * Use case kích hoạt tài khoản nhân viên.
 * Nhân viên dùng token từ email, đặt mật khẩu, tài khoản chuyển sang active.
 */
public class ActivateAccountUseCase implements IUseCase<ActivateAccountCommand, UserResult> {

    private final IUserRepository userRepository;
    private final IPasswordEncoder passwordEncoder;

    /**
     * @param userRepository  repository lưu User
     * @param passwordEncoder port mã hóa mật khẩu
     */
    public ActivateAccountUseCase(IUserRepository userRepository, IPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Xác thực token, kiểm tra hết hạn, đặt password, kích hoạt tài khoản.
     *
     * @param command token kích hoạt và mật khẩu mới
     * @return UserResult sau khi kích hoạt
     * @throws RuntimeException nếu token không hợp lệ hoặc đã hết hạn
     */
    @Override
    public UserResult execute(ActivateAccountCommand command) {
        User user = userRepository.findByActivationToken(command.getToken())
                .orElseThrow(() -> new RuntimeException("Link kích hoạt không hợp lệ"));

        if (user.getActivationExpiresAt() == null
                || LocalDateTime.now().isAfter(user.getActivationExpiresAt())) {
            throw new RuntimeException("Link kích hoạt đã hết hạn");
        }

        String hashedPassword = passwordEncoder.encode(command.getNewPassword());

        User activated = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .passwordHash(hashedPassword)
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .unitId(user.getUnitId())
                .status(UserStatus.active)
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .activationToken(null)
                .activationExpiresAt(null)
                .build();

        User saved = userRepository.save(activated);
        return UserCommandMapper.toResult(saved);
    }
}
