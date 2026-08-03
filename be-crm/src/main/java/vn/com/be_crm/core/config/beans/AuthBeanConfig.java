package vn.com.be_crm.core.config.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.be_crm.application.auth.command.*;
import vn.com.be_crm.application.auth.query.*;
import vn.com.be_crm.core.email.port.IEmailService;
import vn.com.be_crm.core.security.port.IGoogleTokenVerifier;
import vn.com.be_crm.core.security.port.IPasswordEncoder;
import vn.com.be_crm.core.security.port.ITokenProvider;
import vn.com.be_crm.domain.auth.repository.*;

/**
 * Wire các UseCase của module Auth (login, org unit, user, role, permission, N:N) qua @Bean.
 */
@Configuration
public class AuthBeanConfig {

    // ===== Login / Register / Activate =====

    /** @return LoginUseCase được inject IUserRepository, IUserRoleRepository, IPermissionRepository, IPasswordEncoder, ITokenProvider */
    @Bean
    public LoginUseCase loginUseCase(IUserRepository userRepo,
                                     IUserRoleRepository userRoleRepo,
                                     IPermissionRepository permissionRepo,
                                     IPasswordEncoder passwordEncoder,
                                     ITokenProvider tokenProvider) {
        return new LoginUseCase(userRepo, userRoleRepo, permissionRepo, passwordEncoder, tokenProvider);
    }

    /** @return RegisterEmployeeUseCase được inject IUserRepository, IUserRoleRepository, IEmailService */
    @Bean
    public RegisterEmployeeUseCase registerEmployeeUseCase(IUserRepository userRepo,
                                                           IUserRoleRepository userRoleRepo,
                                                           IEmailService emailService) {
        return new RegisterEmployeeUseCase(userRepo, userRoleRepo, emailService);
    }

    /** @return ActivateAccountUseCase được inject IUserRepository, IPasswordEncoder */
    @Bean
    public ActivateAccountUseCase activateAccountUseCase(IUserRepository userRepo,
                                                         IPasswordEncoder passwordEncoder) {
        return new ActivateAccountUseCase(userRepo, passwordEncoder);
    }

    /** @return GoogleLoginUseCase được inject IGoogleTokenVerifier, IUserRepository, IUserRoleRepository, IPermissionRepository, ITokenProvider */
    @Bean
    public GoogleLoginUseCase googleLoginUseCase(IGoogleTokenVerifier googleTokenVerifier,
                                                 IUserRepository userRepo,
                                                 IUserRoleRepository userRoleRepo,
                                                 IPermissionRepository permissionRepo,
                                                 ITokenProvider tokenProvider) {
        return new GoogleLoginUseCase(googleTokenVerifier, userRepo, userRoleRepo, permissionRepo, tokenProvider);
    }

    /** @return ChangePasswordUseCase được inject IUserRepository, IPasswordEncoder */
    @Bean
    public ChangePasswordUseCase changePasswordUseCase(IUserRepository userRepo,
                                                       IPasswordEncoder passwordEncoder) {
        return new ChangePasswordUseCase(userRepo, passwordEncoder);
    }

    // ===== User =====

    /** @return CreateUserUseCase được inject IUserRepository */
    @Bean
    public CreateUserUseCase createUserUseCase(IUserRepository repo) {
        return new CreateUserUseCase(repo);
    }

    /** @return UpdateUserUseCase được inject IUserRepository */
    @Bean
    public UpdateUserUseCase updateUserUseCase(IUserRepository repo) {
        return new UpdateUserUseCase(repo);
    }

    /** @return DeleteUserUseCase được inject IUserRepository */
    @Bean
    public DeleteUserUseCase deleteUserUseCase(IUserRepository repo) {
        return new DeleteUserUseCase(repo);
    }

    /** @return GetUserUseCase được inject IUserRepository */
    @Bean
    public GetUserUseCase getUserUseCase(IUserRepository repo) {
        return new GetUserUseCase(repo);
    }

    /** @return ListUserUseCase được inject IUserRepository */
    @Bean
    public ListUserUseCase listUserUseCase(IUserRepository repo) {
        return new ListUserUseCase(repo);
    }

    // ===== Role =====

    /** @return CreateRoleUseCase được inject IRoleRepository */
    @Bean
    public CreateRoleUseCase createRoleUseCase(IRoleRepository repo) {
        return new CreateRoleUseCase(repo);
    }

    /** @return UpdateRoleUseCase được inject IRoleRepository */
    @Bean
    public UpdateRoleUseCase updateRoleUseCase(IRoleRepository repo) {
        return new UpdateRoleUseCase(repo);
    }

    /** @return DeleteRoleUseCase được inject IRoleRepository */
    @Bean
    public DeleteRoleUseCase deleteRoleUseCase(IRoleRepository repo) {
        return new DeleteRoleUseCase(repo);
    }

    /** @return GetRoleUseCase được inject IRoleRepository */
    @Bean
    public GetRoleUseCase getRoleUseCase(IRoleRepository repo) {
        return new GetRoleUseCase(repo);
    }

    /** @return ListRoleUseCase được inject IRoleRepository */
    @Bean
    public ListRoleUseCase listRoleUseCase(IRoleRepository repo) {
        return new ListRoleUseCase(repo);
    }

    // ===== Permission =====

    /** @return CreatePermissionUseCase được inject IPermissionRepository */
    @Bean
    public CreatePermissionUseCase createPermissionUseCase(IPermissionRepository repo) {
        return new CreatePermissionUseCase(repo);
    }

    /** @return UpdatePermissionUseCase được inject IPermissionRepository */
    @Bean
    public UpdatePermissionUseCase updatePermissionUseCase(IPermissionRepository repo) {
        return new UpdatePermissionUseCase(repo);
    }

    /** @return DeletePermissionUseCase được inject IPermissionRepository */
    @Bean
    public DeletePermissionUseCase deletePermissionUseCase(IPermissionRepository repo) {
        return new DeletePermissionUseCase(repo);
    }

    /** @return GetPermissionUseCase được inject IPermissionRepository */
    @Bean
    public GetPermissionUseCase getPermissionUseCase(IPermissionRepository repo) {
        return new GetPermissionUseCase(repo);
    }

    /** @return ListPermissionUseCase được inject IPermissionRepository */
    @Bean
    public ListPermissionUseCase listPermissionUseCase(IPermissionRepository repo) {
        return new ListPermissionUseCase(repo);
    }

    // ===== RolePermission & UserRole (N:N) =====

    /** @return AssignRolePermissionUseCase được inject IRolePermissionRepository */
    @Bean
    public AssignRolePermissionUseCase assignRolePermissionUseCase(IRolePermissionRepository repo) {
        return new AssignRolePermissionUseCase(repo);
    }

    /** @return RevokeRolePermissionUseCase được inject IRolePermissionRepository */
    @Bean
    public RevokeRolePermissionUseCase revokeRolePermissionUseCase(IRolePermissionRepository repo) {
        return new RevokeRolePermissionUseCase(repo);
    }

    /** @return AssignUserRoleUseCase được inject IUserRoleRepository */
    @Bean
    public AssignUserRoleUseCase assignUserRoleUseCase(IUserRoleRepository repo) {
        return new AssignUserRoleUseCase(repo);
    }

    /** @return RevokeUserRoleUseCase được inject IUserRoleRepository */
    @Bean
    public RevokeUserRoleUseCase revokeUserRoleUseCase(IUserRoleRepository repo) {
        return new RevokeUserRoleUseCase(repo);
    }

    /** @return ListRolePermissionsUseCase được inject IRolePermissionRepository, IPermissionRepository */
    @Bean
    public ListRolePermissionsUseCase listRolePermissionsUseCase(IRolePermissionRepository rolePermRepo,
                                                                 IPermissionRepository permRepo) {
        return new ListRolePermissionsUseCase(rolePermRepo, permRepo);
    }

    /** @return ListRoleMembersUseCase được inject IUserRoleRepository, IUserRepository */
    @Bean
    public ListRoleMembersUseCase listRoleMembersUseCase(IUserRoleRepository userRoleRepo,
                                                         IUserRepository userRepo) {
        return new ListRoleMembersUseCase(userRoleRepo, userRepo);
    }

    /** @return ListUserRoleAssignmentsUseCase được inject IUserRoleRepository */
    @Bean
    public ListUserRoleAssignmentsUseCase listUserRoleAssignmentsUseCase(IUserRoleRepository repo) {
        return new ListUserRoleAssignmentsUseCase(repo);
    }
}
