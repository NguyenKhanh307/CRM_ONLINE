package vn.com.be_crm.infrastructure.activity.repository;

import vn.com.be_crm.infrastructure.shared.util.ListQueryUtils;
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
            String yearFilter = request.getDataAccessFromYear() != null ? " AND YEAR(createdAt) >= :fromYear" : "";
            String ownerFilter = request.getOwnerId() != null ? " AND assignedUserId = :ownerId" : "";
            String searchFilter = ListQueryUtils.likeClause(request.getQ(), "subject");
            var statusVal = ListQueryUtils.parseEnum(vn.com.be_crm.domain.activity.enums.ActivityStatus.class, request.getStatus());
            String statusFilter = statusVal != null ? " AND status = :status" : "";
            String where = " WHERE 1=1" + yearFilter + ownerFilter + searchFilter + statusFilter;
            String orderBy = " ORDER BY " + ListQueryUtils.safeSortBy(request.getSortBy(), "createdAt") + " " + ListQueryUtils.safeSortDir(request.getSortDir());
            var q = s.createQuery("FROM ActivityHibernate" + where + orderBy, ActivityHibernate.class)
                    .setFirstResult(request.getOffset()).setMaxResults(request.getSize());
            var cq = s.createQuery("SELECT COUNT(*) FROM ActivityHibernate" + where, Long.class);
            for (var query : List.of(q, cq)) {
                if (request.getDataAccessFromYear() != null) query.setParameter("fromYear", request.getDataAccessFromYear());
                if (request.getOwnerId() != null) query.setParameter("ownerId", request.getOwnerId());
                if (!searchFilter.isEmpty()) query.setParameter("q", ListQueryUtils.likeParam(request.getQ()));
                if (statusVal != null) query.setParameter("status", statusVal);
            }
            List<Activity> items = q.list().stream().map(mapper::toDomain).collect(Collectors.toList());
            long total = cq.uniqueResult();
            return PageResult.<Activity>builder().items(items).total(total).page(request.getPage()).size(request.getSize()).build();
        }
    }
}
