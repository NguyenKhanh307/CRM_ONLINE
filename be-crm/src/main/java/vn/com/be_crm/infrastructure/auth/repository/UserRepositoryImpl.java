package vn.com.be_crm.infrastructure.auth.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.auth.entity.User;
import vn.com.be_crm.domain.auth.repository.IUserRepository;
import vn.com.be_crm.infrastructure.auth.entity.UserHibernate;
import vn.com.be_crm.infrastructure.auth.mapper.UserHibernateMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Hibernate implementation của IUserRepository.
 * Soft delete: deleteById set deleted_at thay vì xóa thật.
 * findById và findAll chỉ trả về record chưa bị xóa (deleted_at IS NULL).
 */
@Repository
public class UserRepositoryImpl implements IUserRepository {

    private final SessionFactory sessionFactory;
    private final UserHibernateMapper mapper;

    /**
     * @param sessionFactory Hibernate SessionFactory
     * @param mapper         mapper domain ↔ hibernate
     */
    public UserRepositoryImpl(SessionFactory sessionFactory, UserHibernateMapper mapper) {
        this.sessionFactory = sessionFactory;
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
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            UserHibernate h = mapper.toHibernate(user);
            UserHibernate merged = session.merge(h);
            tx.commit();
            return mapper.toDomain(merged);
        }
    }

    /**
     * Tìm User theo ID, chỉ trả về nếu chưa bị xóa mềm.
     *
     * @param id ID người dùng
     * @return Optional chứa User nếu tìm thấy và chưa xóa
     */
    @Override
    public Optional<User> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            UserHibernate h = session.find(UserHibernate.class, id);
            if (h == null || h.getDeletedAt() != null) return Optional.empty();
            return Optional.of(mapper.toDomain(h));
        }
    }

    /**
     * Tìm User theo email, chỉ trả về nếu chưa bị xóa mềm.
     *
     * @param email địa chỉ email cần tìm
     * @return Optional chứa User nếu tìm thấy và chưa xóa
     */
    @Override
    public Optional<User> findByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            UserHibernate h = session.createQuery(
                    "FROM UserHibernate WHERE email = :email AND deletedAt IS NULL", UserHibernate.class)
                    .setParameter("email", email)
                    .uniqueResult();
            if (h == null) return Optional.empty();
            return Optional.of(mapper.toDomain(h));
        }
    }

    /**
     * Xóa mềm User bằng cách set deleted_at = now().
     *
     * @param id ID người dùng cần xóa mềm
     */
    @Override
    public void deleteById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            UserHibernate h = session.find(UserHibernate.class, id);
            if (h != null) {
                h.setDeletedAt(LocalDateTime.now());
                session.merge(h);
            }
            tx.commit();
        }
    }

    /**
     * Lấy danh sách User chưa xóa có phân trang.
     *
     * @param request tham số phân trang
     * @return PageResult chứa danh sách User
     */
    @Override
    public PageResult<User> findAll(PageRequest request) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "FROM UserHibernate WHERE deletedAt IS NULL ORDER BY " + request.getSortBy() + " " + request.getSortDir();
            List<User> items = session.createQuery(hql, UserHibernate.class)
                    .setFirstResult(request.getOffset())
                    .setMaxResults(request.getSize())
                    .list()
                    .stream().map(mapper::toDomain).collect(Collectors.toList());
            long total = session.createQuery(
                    "SELECT COUNT(u) FROM UserHibernate u WHERE u.deletedAt IS NULL", Long.class)
                    .uniqueResult();
            return PageResult.<User>builder()
                    .items(items).total(total).page(request.getPage()).size(request.getSize()).build();
        }
    }
}
