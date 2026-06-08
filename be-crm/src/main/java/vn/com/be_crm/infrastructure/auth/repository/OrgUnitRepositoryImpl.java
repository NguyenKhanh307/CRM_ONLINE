package vn.com.be_crm.infrastructure.auth.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.auth.entity.OrgUnit;
import vn.com.be_crm.domain.auth.repository.IOrgUnitRepository;
import vn.com.be_crm.infrastructure.auth.entity.OrgUnitHibernate;
import vn.com.be_crm.infrastructure.auth.mapper.OrgUnitHibernateMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Hibernate implementation của IOrgUnitRepository.
 */
@Repository
public class OrgUnitRepositoryImpl implements IOrgUnitRepository {

    private final SessionFactory sessionFactory;
    private final OrgUnitHibernateMapper mapper;

    /**
     * @param sessionFactory Hibernate SessionFactory
     * @param mapper         mapper domain ↔ hibernate
     */
    public OrgUnitRepositoryImpl(SessionFactory sessionFactory, OrgUnitHibernateMapper mapper) {
        this.sessionFactory = sessionFactory;
        this.mapper = mapper;
    }

    /**
     * Lưu mới hoặc cập nhật OrgUnit. Dùng try-with-resources để đảm bảo Session đóng.
     *
     * @param orgUnit domain entity cần lưu
     * @return domain entity sau khi lưu
     */
    @Override
    public OrgUnit save(OrgUnit orgUnit) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            OrgUnitHibernate h = mapper.toHibernate(orgUnit);
            OrgUnitHibernate merged = session.merge(h);
            tx.commit();
            return mapper.toDomain(merged);
        }
    }

    /**
     * Tìm OrgUnit theo ID.
     *
     * @param id ID đơn vị
     * @return Optional chứa OrgUnit nếu tìm thấy
     */
    @Override
    public Optional<OrgUnit> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            OrgUnitHibernate h = session.find(OrgUnitHibernate.class, id);
            return Optional.ofNullable(h).map(mapper::toDomain);
        }
    }

    /**
     * Xóa OrgUnit theo ID. Không làm gì nếu không tìm thấy.
     *
     * @param id ID đơn vị cần xóa
     */
    @Override
    public void deleteById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            OrgUnitHibernate h = session.find(OrgUnitHibernate.class, id);
            if (h != null) session.remove(h);
            tx.commit();
        }
    }

    /**
     * Lấy danh sách OrgUnit có phân trang và sắp xếp.
     *
     * @param request tham số phân trang
     * @return PageResult chứa danh sách OrgUnit
     */
    @Override
    public PageResult<OrgUnit> findAll(PageRequest request) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "FROM OrgUnitHibernate ORDER BY " + request.getSortBy() + " " + request.getSortDir();
            List<OrgUnit> items = session.createQuery(hql, OrgUnitHibernate.class)
                    .setFirstResult(request.getOffset())
                    .setMaxResults(request.getSize())
                    .list()
                    .stream().map(mapper::toDomain).collect(Collectors.toList());
            long total = session.createQuery("SELECT COUNT(o) FROM OrgUnitHibernate o", Long.class)
                    .uniqueResult();
            return PageResult.<OrgUnit>builder()
                    .items(items).total(total).page(request.getPage()).size(request.getSize()).build();
        }
    }
}
