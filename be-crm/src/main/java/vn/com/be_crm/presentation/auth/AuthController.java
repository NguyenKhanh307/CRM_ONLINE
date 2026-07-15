package vn.com.be_crm.presentation.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.auth.command.ActivateAccountUseCase;
import vn.com.be_crm.application.auth.command.ChangePasswordUseCase;
import vn.com.be_crm.application.auth.command.GoogleLoginCommand;
import vn.com.be_crm.application.auth.command.GoogleLoginUseCase;
import vn.com.be_crm.application.auth.command.LoginCommand;
import vn.com.be_crm.application.auth.command.LoginUseCase;
import vn.com.be_crm.application.auth.command.RegisterEmployeeUseCase;
import vn.com.be_crm.application.auth.command.UpdateUserUseCase;
import vn.com.be_crm.application.auth.dto.ActivateAccountCommand;
import vn.com.be_crm.application.auth.dto.ChangePasswordCommand;
import vn.com.be_crm.application.auth.dto.LoginResult;
import vn.com.be_crm.application.auth.dto.RegisterEmployeeCommand;
import vn.com.be_crm.application.auth.dto.UpdateUserCommand;
import vn.com.be_crm.application.auth.dto.UserResult;
import vn.com.be_crm.application.auth.query.GetUserUseCase;
import vn.com.be_crm.presentation.auth.request.ActivateAccountRequest;
import vn.com.be_crm.presentation.auth.request.ChangePasswordRequest;
import vn.com.be_crm.presentation.auth.request.GoogleLoginRequest;
import vn.com.be_crm.presentation.auth.request.LoginRequest;
import vn.com.be_crm.presentation.auth.request.RegisterEmployeeRequest;
import vn.com.be_crm.presentation.auth.request.UpdateProfileRequest;
import vn.com.be_crm.presentation.shared.ApiResponse;

/**
 * Endpoint xác thực — đăng nhập, đăng ký nhân viên, kích hoạt tài khoản.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegisterEmployeeUseCase registerEmployeeUseCase;
    private final ActivateAccountUseCase activateAccountUseCase;
    private final GoogleLoginUseCase googleLoginUseCase;
    private final GetUserUseCase getUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    /**
     * @param loginUseCase             use case đăng nhập
     * @param registerEmployeeUseCase  use case đăng ký nhân viên
     * @param activateAccountUseCase   use case kích hoạt tài khoản
     * @param googleLoginUseCase       use case đăng nhập bằng Google
     * @param getUserUseCase           use case đọc thông tin người dùng
     * @param updateUserUseCase        use case cập nhật người dùng
     * @param changePasswordUseCase    use case đổi mật khẩu
     */
    public AuthController(LoginUseCase loginUseCase,
                          RegisterEmployeeUseCase registerEmployeeUseCase,
                          ActivateAccountUseCase activateAccountUseCase,
                          GoogleLoginUseCase googleLoginUseCase,
                          GetUserUseCase getUserUseCase,
                          UpdateUserUseCase updateUserUseCase,
                          ChangePasswordUseCase changePasswordUseCase) {
        this.loginUseCase = loginUseCase;
        this.registerEmployeeUseCase = registerEmployeeUseCase;
        this.activateAccountUseCase = activateAccountUseCase;
        this.googleLoginUseCase = googleLoginUseCase;
        this.getUserUseCase = getUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
    }

    /**
     * Đăng nhập bằng email và password, trả về JWT token.
     *
     * @param request body chứa email và password
     * @return 200 OK với LoginResult (token + thông tin user)
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResult>> login(@Valid @RequestBody LoginRequest request) {
        LoginCommand command = new LoginCommand(request.getEmail(), request.getPassword());
        LoginResult result = loginUseCase.execute(command);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    /**
     * Admin đăng ký tài khoản nhân viên mới — gửi activation email.
     * Yêu cầu JWT hợp lệ (Bearer token).
     *
     * @param request body chứa email, fullName, phone, unitId, roleId
     * @return 201 Created với UserResult nhân viên vừa tạo
     */
    @PostMapping("/register-employee")
    public ResponseEntity<ApiResponse<UserResult>> registerEmployee(
            @Valid @RequestBody RegisterEmployeeRequest request) {
        RegisterEmployeeCommand command = RegisterEmployeeCommand.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .unitId(request.getUnitId())
                .roleId(request.getRoleId())
                .frontendBaseUrl(frontendBaseUrl)
                .build();
        UserResult result = registerEmployeeUseCase.execute(command);
        return ResponseEntity.status(201).body(ApiResponse.created(result));
    }

    /**
     * Nhân viên kích hoạt tài khoản — đặt mật khẩu lần đầu.
     * Không yêu cầu JWT (public endpoint).
     *
     * @param request body chứa token và newPassword
     * @return 200 OK với UserResult đã kích hoạt
     */
    @PostMapping("/activate")
    public ResponseEntity<ApiResponse<UserResult>> activate(
            @Valid @RequestBody ActivateAccountRequest request) {
        ActivateAccountCommand command = ActivateAccountCommand.builder()
                .token(request.getToken())
                .newPassword(request.getNewPassword())
                .build();
        UserResult result = activateAccountUseCase.execute(command);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    /**
     * Đăng nhập bằng Google — xác thực ID token, chỉ cho vào nếu email có trong hệ thống.
     * Không yêu cầu JWT (public endpoint).
     *
     * @param request body chứa Google ID token
     * @return 200 OK với LoginResult (token + thông tin user)
     */
    @PostMapping("/google")
    public ResponseEntity<ApiResponse<LoginResult>> googleLogin(
            @Valid @RequestBody GoogleLoginRequest request) {
        LoginResult result = googleLoginUseCase.execute(new GoogleLoginCommand(request.getIdToken()));
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    /**
     * Lấy hồ sơ của người dùng đang đăng nhập.
     *
     * @param req HTTP request (đọc userId do JwtAuthFilter gắn)
     * @return 200 OK với UserResult của chính mình
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResult>> getMyProfile(HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        UserResult result = getUserUseCase.execute(userId);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    /**
     * Người dùng tự cập nhật hồ sơ của mình (họ tên, SĐT, ảnh đại diện).
     * Các trường nhạy cảm (vai trò, đơn vị, năm truy cập) không nhận từ đây.
     *
     * @param request body chứa fullName, phone, avatarUrl
     * @param req     HTTP request (đọc userId do JwtAuthFilter gắn)
     * @return 200 OK với UserResult sau khi cập nhật
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResult>> updateMyProfile(
            @Valid @RequestBody UpdateProfileRequest request, HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        UpdateUserCommand command = UpdateUserCommand.builder()
                .id(userId)
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .avatarUrl(request.getAvatarUrl())
                .build();
        UserResult result = updateUserUseCase.execute(command);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    /**
     * Người dùng tự đổi mật khẩu — xác minh mật khẩu hiện tại trước khi đổi.
     *
     * @param request body chứa currentPassword, newPassword
     * @param req     HTTP request (đọc userId do JwtAuthFilter gắn)
     * @return 200 OK khi đổi thành công
     */
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request, HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        ChangePasswordCommand command = ChangePasswordCommand.builder()
                .userId(userId)
                .currentPassword(request.getCurrentPassword())
                .newPassword(request.getNewPassword())
                .build();
        changePasswordUseCase.execute(command);
        return ResponseEntity.ok(ApiResponse.<Void>ok(null));
    }
}
