package vn.com.be_crm.presentation.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * HTTP request body cho POST /api/auth/google.
 */
@Getter
@NoArgsConstructor
public class GoogleLoginRequest {

    /** ID token do Google Identity Services cấp ở FE. */
    @NotBlank
    private String idToken;
}
