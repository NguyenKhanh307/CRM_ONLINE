package vn.com.be_crm.presentation.auth;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.auth.command.ActivateAccountUseCase;
import vn.com.be_crm.application.auth.command.LoginCommand;
import vn.com.be_crm.application.auth.command.LoginUseCase;
import vn.com.be_crm.application.auth.command.RegisterEmployeeUseCase;
import vn.com.be_crm.application.auth.dto.ActivateAccountCommand;
import vn.com.be_crm.application.auth.dto.LoginResult;
import vn.com.be_crm.application.auth.dto.RegisterEmployeeCommand;
import vn.com.be_crm.application.auth.dto.UserResult;
import vn.com.be_crm.presentation.auth.request.ActivateAccountRequest;
import vn.com.be_crm.presentation.auth.request.LoginRequest;
import vn.com.be_crm.presentation.auth.request.RegisterEmployeeRequest;
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

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    /**
     * @param loginUseCase             use case đăng nhập
     * @param registerEmployeeUseCase  use case đăng ký nhân viên
     * @param activateAccountUseCase   use case kích hoạt tài khoản
     */
    public AuthController(LoginUseCase loginUseCase,
                          RegisterEmployeeUseCase registerEmployeeUseCase,
                          ActivateAccountUseCase activateAccountUseCase) {
        this.loginUseCase = loginUseCase;
        this.registerEmployeeUseCase = registerEmployeeUseCase;
        this.activateAccountUseCase = activateAccountUseCase;
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
}
