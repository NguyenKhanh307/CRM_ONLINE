package vn.com.be_crm.application.auth.query;

import vn.com.be_crm.application.auth.dto.UserResult;
import vn.com.be_crm.application.auth.mapper.UserCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.auth.repository.IUserRepository;
import vn.com.be_crm.domain.auth.repository.IUserRoleRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Use case lấy danh sách thành viên (user) thuộc một vai trò.
 */
public class ListRoleMembersUseCase implements IUseCase<Long, List<UserResult>> {

    private final IUserRoleRepository userRoleRepository;
    private final IUserRepository userRepository;

    /**
     * @param userRoleRepository port lưu trữ UserRole
     * @param userRepository     port lưu trữ User
     */
    public ListRoleMembersUseCase(IUserRoleRepository userRoleRepository,
                                   IUserRepository userRepository) {
        this.userRoleRepository = userRoleRepository;
        this.userRepository = userRepository;
    }

    /**
     * Lấy danh sách UserResult của những người dùng thuộc vai trò roleId.
     *
     * @param roleId ID vai trò
     * @return danh sách UserResult thuộc vai trò
     */
    @Override
    public List<UserResult> execute(Long roleId) {
        return userRoleRepository.findByRoleId(roleId).stream()
                .map(ur -> userRepository.findById(ur.getUserId()))
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .map(UserCommandMapper::toResult)
                .collect(Collectors.toList());
    }
}
