package vn.com.be_crm.infrastructure.activity.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.activity.entity.Activity;
import vn.com.be_crm.domain.activity.repository.IActivityRepository;
import vn.com.be_crm.infrastructure.activity.entity.ActivityHibernate;
import vn.com.be_crm.infrastructure.activity.mapper.ActivityHibernateMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Hibernate implementation của IActivityRepository.
 */
@Repository
public class ActivityRepositoryImpl implements IActivityRepository {

    private final SessionFactory sf;
    private final ActivityHibernateMapper mapper;

    /**
     * @param sf Hibernate SessionFactory
     * @param mapper         mapper domain ↔ hibernate
     */
    public ActivityRepositoryImpl(SessionFactory sf, ActivityHibernateMapper mapper) {
        this.sf = sf;
        this.mapper = mapper;
    }

    /**
     * Lưu mới hoặc cập nhật Activity.
     *
     * @param activity domain entity cần lưu
     * @return domain entity sau khi lưu
     */
    @Override
    public Activity save(Activity activity) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            ActivityHibernate merged = s.merge(mapper.toHibernate(activity));
            tx.commit();
            return mapper.toDomain(merged);
        }
    }

    /**
     * Tìm Activity theo ID.
     *
     * @param id ID hoạt động
     * @return Optional chứa Activity nếu tìm thấy
     */
    @Override
    public Optional<Activity> findById(Long id) {
        try (Session s = sf.openSession()) {
            ActivityHibernate h = s.find(ActivityHibernate.class, id);
            return Optional.ofNullable(h).map(mapper::toDomain);
        }
    }

    /**
     * Xóa Activity theo ID. Không làm gì nếu không tìm thấy.
     *
     * @param id ID hoạt động cần xóa
     */
    @Override
    public void deleteById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            ActivityHibernate h = s.find(ActivityHibernate.class, id);
            if (h != null) s.remove(h);
            tx.commit();
        }
    }

    /**
     * Lấy danh sách Activity có phân trang.
     *
     * @param request tham số phân trang
     * @return PageResult chứa danh sách Activity
     */
    @Override
    public PageResult<Activity> findAll(PageRequest request) {
        try (Session s = sf.openSession()) {
            String yearFilter = request.getDataAccessFromYear() != null ? " WHERE YEAR(createdAt) >= :fromYear" : "";
            var q = s.createQuery(
                    "FROM ActivityHibernate" + yearFilter + " ORDER BY " + request.getSortBy() + " " + request.getSortDir(),
                    ActivityHibernate.class)
                    .setFirstResult(request.getOffset()).setMaxResults(request.getSize());
            if (request.getDataAccessFromYear() != null) q.setParameter("fromYear", request.getDataAccessFromYear());
            List<Activity> items = q.list().stream().map(mapper::toDomain).collect(Collectors.toList());
            var cq = s.createQuery("SELECT COUNT(a) FROM ActivityHibernate a" + (request.getDataAccessFromYear() != null ? " WHERE YEAR(a.createdAt) >= :fromYear" : ""), Long.class);
            if (request.getDataAccessFromYear() != null) cq.setParameter("fromYear", request.getDataAccessFromYear());
            long total = cq.uniqueResult();
            return PageResult.<Activity>builder()
                    .items(items).total(total).page(request.getPage()).size(request.getSize()).build();
        }
    }
}
