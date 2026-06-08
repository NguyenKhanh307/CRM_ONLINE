package vn.com.be_crm.presentation.auth;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.auth.command.LoginCommand;
import vn.com.be_crm.application.auth.command.LoginUseCase;
import vn.com.be_crm.application.auth.dto.LoginResult;
import vn.com.be_crm.presentation.auth.request.LoginRequest;
import vn.com.be_crm.presentation.shared.ApiResponse;

/**
 * Endpoint xác thực — đăng nhập và cấp JWT token.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;

    /**
     * @param loginUseCase use case đăng nhập
     */
    public AuthController(LoginUseCase loginUseCase) {
        this.loginUseCase = loginUseCase;
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
}
