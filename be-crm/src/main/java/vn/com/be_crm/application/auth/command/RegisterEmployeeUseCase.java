package vn.com.be_crm.application.auth.command;

import vn.com.be_crm.application.auth.dto.RegisterEmployeeCommand;
import vn.com.be_crm.application.auth.dto.UserResult;
import vn.com.be_crm.application.auth.mapper.UserCommandMapper;
import vn.com.be_crm.application.shared.email.IEmailService;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.auth.entity.User;
import vn.com.be_crm.domain.auth.entity.UserRole;
import vn.com.be_crm.domain.auth.enums.UserStatus;
import vn.com.be_crm.domain.auth.repository.IUserRepository;
import vn.com.be_crm.domain.auth.repository.IUserRoleRepository;
import vn.com.be_crm.domain.shared.exception.DomainException;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Use case admin đăng ký tài khoản nhân viên mới.
 * Tạo User với status=inactive, sinh activation token hết hạn sau 1 ngày,
 * gán role ngay lập tức, rồi gửi email kích hoạt tới nhân viên.
 */
public class RegisterEmployeeUseCase implements IUseCase<RegisterEmployeeCommand, UserResult> {

    private final IUserRepository userRepository;
    private final IUserRoleRepository userRoleRepository;
    private final IEmailService emailService;

    /**
     * @param userRepository     repository lưu User
     * @param userRoleRepository repository gán vai trò
     * @param emailService       port gửi email
     */
    public RegisterEmployeeUseCase(IUserRepository userRepository,
                                   IUserRoleRepository userRoleRepository,
                                   IEmailService emailService) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.emailService = emailService;
    }

    /**
     * Đăng ký nhân viên mới: kiểm tra email trùng, tạo User inactive,
     * gán role, gửi activation email.
     *
     * @param command dữ liệu đăng ký từ admin
     * @return UserResult nhân viên vừa tạo
     * @throws DomainException nếu email đã tồn tại
     */
    @Override
    public UserResult execute(RegisterEmployeeCommand command) {
        userRepository.findByEmail(command.getEmail()).ifPresent(u -> {
            // DomainException → HTTP 400 với message nghiệp vụ (RuntimeException sẽ rơi vào handler chung → 500)
            throw new DomainException("Email \"" + command.getEmail() + "\" đã được sử dụng");
        });

        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(1);

        User newUser = User.builder()
                .email(command.getEmail())
                .passwordHash("")
                .fullName(command.getFullName())
                .phone(command.getPhone())
                .status(UserStatus.inactive)
                .activationToken(token)
                .activationExpiresAt(expiresAt)
                .build();

        User saved = userRepository.save(newUser);

        if (command.getRoleId() != null) {
            userRoleRepository.save(UserRole.builder()
                    .userId(saved.getId())
                    .roleId(command.getRoleId())
                    .build());
        }

        String activationLink = command.getFrontendBaseUrl() + "/activate?token=" + token;
        emailService.sendActivationEmail(saved.getEmail(), saved.getFullName(), activationLink);

        return UserCommandMapper.toResult(saved);
    }
}
