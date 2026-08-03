package vn.com.be_crm.infrastructure.auth.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.page.PageResult;
import vn.com.be_crm.domain.auth.entity.User;
import vn.com.be_crm.domain.auth.repository.IUserRepository;
import vn.com.be_crm.infrastructure.auth.entity.UserHibernate;
import vn.com.be_crm.infrastructure.auth.mapper.UserHibernateMapper;
import vn.com.be_crm.core.util.ListQueryUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import vn.com.be_crm.core.tx.impl.TxSupport;

/**
 * Hibernate implementation của IUserRepository.
 * Soft delete: deleteById set deleted_at thay vì xóa thật.
 * findById và findAll chỉ trả về record chưa bị xóa (deleted_at IS NULL).
 */
@Repository
public class UserRepositoryImpl implements IUserRepository {

    private final SessionFactory sf;
    private final UserHibernateMapper mapper;

    /**
     * @param sf     Hibernate SessionFactory
     * @param mapper mapper domain ↔ hibernate
     */
    public UserRepositoryImpl(SessionFactory sf, UserHibernateMapper mapper) {
        this.sf = sf;
        this.mapper = mapper;
    }

    /**
     * Lưu mới hoặc cập nhật User bằng merge (Hibernate 7).
     *
     * @param user domain entity cần lưu
     * @return domain entity sau khi lưu
     */
    @Override
    public User save(User user) {
        return TxSupport.write(sf, s -> {
            UserHibernate h = mapper.toHibernate(user);
            UserHibernate merged = s.merge(h);
            return mapper.toDomain(merged);
        });
    }

    /**
     * Tìm User theo ID, chỉ trả về nếu chưa bị xóa mềm.
     *
     * @param id ID người dùng
     * @return Optional chứa User nếu tìm thấy và chưa xóa
     */
    @Override
    public Optional<User> findById(Long id) {
        return TxSupport.read(sf, s -> {
            UserHibernate h = s.find(UserHibernate.class, id);
            if (h == null || h.getDeletedAt() != null) return Optional.empty();
            return Optional.of(mapper.toDomain(h));
        });
    }

    /**
     * Tìm User theo email, chỉ trả về nếu chưa bị xóa mềm.
     *
     * @param email địa chỉ email cần tìm
     * @return Optional chứa User nếu tìm thấy và chưa xóa
     */
    @Override
    public Optional<User> findByEmail(String email) {
        return TxSupport.read(sf, s -> {
            UserHibernate h = s.createQuery(
                    "FROM UserHibernate WHERE email = :email AND deletedAt IS NULL", UserHibernate.class)
                    .setParameter("email", email)
                    .uniqueResult();
            if (h == null) return Optional.empty();
            return Optional.of(mapper.toDomain(h));
        });
    }

    /**
     * Xóa mềm User bằng cách set deleted_at = now().
     *
     * @param id ID người dùng cần xóa mềm
     */
    @Override
    public void deleteById(Long id) {
        TxSupport.writeVoid(sf, s -> {
            UserHibernate h = s.find(UserHibernate.class, id);
            if (h != null) {
                h.setDeletedAt(LocalDateTime.now());
                s.merge(h);
            }
            });
    }

    /**
     * Tìm User theo activation token, chỉ trả về nếu chưa bị xóa mềm.
     *
     * @param token activation token cần tra cứu
     * @return Optional chứa User nếu tìm thấy
     */
    @Override
    public Optional<User> findByActivationToken(String token) {
        return TxSupport.read(sf, s -> {
            UserHibernate h = s.createQuery(
                    "FROM UserHibernate WHERE activationToken = :token AND deletedAt IS NULL",
                    UserHibernate.class)
                    .setParameter("token", token)
                    .uniqueResult();
            if (h == null) return Optional.empty();
            return Optional.of(mapper.toDomain(h));
        });
    }

    /**
     * Lấy danh sách User chưa xóa có phân trang.
     *
     * @param request tham số phân trang
     * @return PageResult chứa danh sách User
     */
    @Override
    public PageResult<User> findAll(PageRequest request) {
        return TxSupport.read(sf, s -> {
            // status là field enum — phải bind enum, bind String sẽ ném QueryArgumentException
            var statusVal = ListQueryUtils.parseEnum(vn.com.be_crm.domain.auth.enums.UserStatus.class, request.getStatus());
            String statusFilter = statusVal != null ? " AND status = :status" : "";
            String hql = "FROM UserHibernate WHERE deletedAt IS NULL" + statusFilter + " ORDER BY " + request.getSortBy() + " " + request.getSortDir();
            var q = s.createQuery(hql, UserHibernate.class)
                    .setFirstResult(request.getOffset())
                    .setMaxResults(request.getSize());
            if (statusVal != null) q.setParameter("status", statusVal);
            List<User> items = q.list().stream().map(mapper::toDomain).collect(Collectors.toList());
            String countHql = "SELECT COUNT(u) FROM UserHibernate u WHERE u.deletedAt IS NULL" + statusFilter;
            var cq = s.createQuery(countHql, Long.class);
            if (statusVal != null) cq.setParameter("status", statusVal);
            long total = cq.uniqueResult();
            return PageResult.<User>builder()
                    .items(items).total(total).page(request.getPage()).size(request.getSize()).build();
        });
    }
}
