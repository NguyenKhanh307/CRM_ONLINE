package vn.com.be_crm.infrastructure.auth.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.page.PageResult;
import vn.com.be_crm.domain.auth.entity.Role;
import vn.com.be_crm.domain.auth.repository.IRoleRepository;
import vn.com.be_crm.infrastructure.auth.entity.RoleHibernate;
import vn.com.be_crm.infrastructure.auth.mapper.RoleHibernateMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import vn.com.be_crm.core.tx.impl.TxSupport;

/**
 * Hibernate implementation của IRoleRepository.
 */
@Repository
public class RoleRepositoryImpl implements IRoleRepository {

    private final SessionFactory sf;
    private final RoleHibernateMapper mapper;

    /**
     * @param sf Hibernate SessionFactory
     * @param mapper         mapper domain ↔ hibernate
     */
    public RoleRepositoryImpl(SessionFactory sf, RoleHibernateMapper mapper) {
        this.sf = sf;
        this.mapper = mapper;
    }

    /**
     * Lưu mới hoặc cập nhật Role.
     *
     * @param role domain entity cần lưu
     * @return domain entity sau khi lưu
     */
    @Override
    public Role save(Role role) {
        return TxSupport.write(sf, s -> {
            RoleHibernate merged = s.merge(mapper.toHibernate(role));
            return mapper.toDomain(merged);
        });
    }

    /**
     * Tìm Role theo ID.
     *
     * @param id ID vai trò
     * @return Optional chứa Role nếu tìm thấy
     */
    @Override
    public Optional<Role> findById(Long id) {
        return TxSupport.read(sf, s -> {
            RoleHibernate h = s.find(RoleHibernate.class, id);
            return Optional.ofNullable(h).map(mapper::toDomain);
        });
    }

    /**
     * Xóa Role theo ID. Không làm gì nếu không tìm thấy.
     *
     * @param id ID vai trò cần xóa
     */
    @Override
    public void deleteById(Long id) {
        TxSupport.writeVoid(sf, s -> {
            RoleHibernate h = s.find(RoleHibernate.class, id);
            if (h != null) s.remove(h);
            });
    }

    /**
     * Lấy danh sách Role có phân trang.
     *
     * @param request tham số phân trang
     * @return PageResult chứa danh sách Role
     */
    @Override
    public PageResult<Role> findAll(PageRequest request) {
        return TxSupport.read(sf, s -> {
            String hql = "FROM RoleHibernate ORDER BY " + request.getSortBy() + " " + request.getSortDir();
            List<Role> items = s.createQuery(hql, RoleHibernate.class)
                    .setFirstResult(request.getOffset())
                    .setMaxResults(request.getSize())
                    .list().stream().map(mapper::toDomain).collect(Collectors.toList());
            long total = s.createQuery("SELECT COUNT(r) FROM RoleHibernate r", Long.class).uniqueResult();
            return PageResult.<Role>builder()
                    .items(items).total(total).page(request.getPage()).size(request.getSize()).build();
        });
    }
}
