package vn.com.be_crm.application.auth.command;

import vn.com.be_crm.application.auth.dto.ChangePasswordCommand;
import vn.com.be_crm.core.security.port.IPasswordEncoder;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.auth.entity.User;
import vn.com.be_crm.domain.auth.repository.IUserRepository;
import vn.com.be_crm.core.error.frontend.DomainException;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/**
 * Use case đổi mật khẩu cho người dùng đang đăng nhập.
 * Xác minh mật khẩu hiện tại rồi mới ghi đè mật khẩu mới đã hash.
 */
public class ChangePasswordUseCase implements IUseCase<ChangePasswordCommand, Void> {

    private final IUserRepository userRepository;
    private final IPasswordEncoder passwordEncoder;

    /**
     * @param userRepository  repository lưu User
     * @param passwordEncoder port kiểm tra + mã hóa mật khẩu
     */
    public ChangePasswordUseCase(IUserRepository userRepository, IPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Tìm user theo ID, kiểm tra mật khẩu hiện tại, lưu mật khẩu mới.
     *
     * @param command userId + mật khẩu hiện tại + mật khẩu mới
     * @return null (không có output)
     * @throws NotFoundException nếu không tìm thấy User
     * @throws DomainException   nếu mật khẩu hiện tại không đúng
     */
    @Override
    public Void execute(ChangePasswordCommand command) {
        User existing = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new NotFoundException("User", command.getUserId()));

        if (!passwordEncoder.matches(command.getCurrentPassword(), existing.getPasswordHash())) {
            throw new DomainException("Mật khẩu hiện tại không đúng");
        }

        String hashed = passwordEncoder.encode(command.getNewPassword());
        userRepository.save(existing.toBuilder().passwordHash(hashed).build());
        return null;
    }
}
