package vn.com.be_crm.infrastructure.lead.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.application.shared.dto.DeletedItemResult;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.lead.entity.Lead;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;
import vn.com.be_crm.infrastructure.lead.entity.LeadHibernate;
import vn.com.be_crm.infrastructure.lead.mapper.LeadHibernateMapper;
import vn.com.be_crm.infrastructure.shared.util.ListQueryUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import vn.com.be_crm.infrastructure.shared.tx.TxSupport;

/**
 * Hibernate implementation của ILeadRepository.
 * Soft delete: deleteById set deleted_at = now().
 */
@Repository
public class LeadRepositoryImpl implements ILeadRepository {
    private final SessionFactory sf;
    private final LeadHibernateMapper mapper;

    /**
     * @param sf     Hibernate SessionFactory
     * @param mapper mapper domain ↔ hibernate
     */
    public LeadRepositoryImpl(SessionFactory sf, LeadHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /** Lưu mới hoặc cập nhật Lead. @param l domain entity @return entity sau khi lưu */
    @Override public Lead save(Lead l) {
        return TxSupport.write(sf, s -> {
            LeadHibernate m = s.merge(mapper.toHibernate(l));
            return mapper.toDomain(m);
        });
    }

    /** Tìm Lead theo ID — chỉ trả về nếu chưa xóa mềm. @param id ID @return Optional */
    @Override public Optional<Lead> findById(Long id) {
        return TxSupport.read(sf, s -> {
            LeadHibernate h = s.find(LeadHibernate.class, id);
            if (h == null || h.getDeletedAt() != null) return Optional.empty();
            return Optional.of(mapper.toDomain(h));
        });
    }

    /** Xóa mềm Lead, ghi nhận người xóa. @param id ID @param deletedBy userId người xóa */
    @Override public void deleteById(Long id, Long deletedBy) {
        TxSupport.writeVoid(sf, s -> {
            LeadHibernate h = s.find(LeadHibernate.class, id);
            if (h != null) { h.setDeletedAt(LocalDateTime.now()); h.setDeletedBy(deletedBy); s.merge(h); }
            });
    }

    /** Lấy danh sách Lead trong thùng rác (30 ngày). @param userId ID người dùng @param isAdmin admin thấy tất cả @param req phân trang */
    @Override public PageResult<DeletedItemResult> findDeleted(Long userId, boolean isAdmin, PageRequest req) {
        return TxSupport.read(sf, s -> {
            // Mốc 30 ngày: chỉ hiện bản ghi đã xóa trong 30 ngày gần đây
            LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
            // Không phải admin → chỉ xem bản ghi do chính mình xóa
            String userFilter = isAdmin ? "" : " AND l.deleted_by = :userId";
            // Native query LEFT JOIN users để lấy tên người xóa
            String sql = "SELECT l.id, l.name, l.deleted_at, u.full_name FROM leads l" +
                    " LEFT JOIN users u ON u.id = l.deleted_by" +
                    " WHERE l.deleted_at IS NOT NULL AND l.deleted_at >= :cutoff AND l.is_purged = 0" +
                    userFilter + " ORDER BY l.deleted_at DESC";
            // Chạy query phân trang rồi map Object[] → DeletedItemResult (xử lý Timestamp của TiDB)
            var q = s.createNativeQuery(sql, Object[].class)
                    .setParameter("cutoff", cutoff)
                    .setFirstResult(req.getOffset()).setMaxResults(req.getSize());
            if (!isAdmin) q.setParameter("userId", userId);
            List<DeletedItemResult> items = q.list().stream()
                    .map(row -> new DeletedItemResult(
                            ((Number) row[0]).longValue(), (String) row[1],
                            row[2] instanceof Timestamp ts ? ts.toLocalDateTime() : (LocalDateTime) row[2],
                            (String) row[3]))
                    .collect(Collectors.toList());
            // Query đếm tổng số bản ghi đã xóa để phân trang
            String countSql = "SELECT COUNT(*) FROM leads l WHERE l.deleted_at IS NOT NULL AND l.deleted_at >= :cutoff AND l.is_purged = 0" + userFilter;
            var cq = s.createNativeQuery(countSql, Object.class).setParameter("cutoff", cutoff);
            if (!isAdmin) cq.setParameter("userId", userId);
            long total = ((Number) cq.uniqueResult()).longValue();
            return PageResult.<DeletedItemResult>builder().items(items).total(total).page(req.getPage()).size(req.getSize()).build();
        });
    }

    /** Khôi phục Lead từ thùng rác. @param id ID */
    @Override public void restoreById(Long id) {
        TxSupport.writeVoid(sf, s -> {
            LeadHibernate h = s.find(LeadHibernate.class, id);
            if (h != null) { h.setDeletedAt(null); h.setDeletedBy(null); h.setPurged(false); s.merge(h); }
            });
    }

    /** Ẩn Lead khỏi thùng rác (is_purged = true). @param id ID */
    @Override public void purgeById(Long id) {
        TxSupport.writeVoid(sf, s -> {
            LeadHibernate h = s.find(LeadHibernate.class, id);
            if (h != null) { h.setPurged(true); s.merge(h); }
            });
    }

    /** Tìm Lead theo số điện thoại (chưa xóa mềm). @param phone số điện thoại @return Optional */
    @Override public Optional<Lead> findByPhone(String phone) {
        return TxSupport.read(sf, s -> {
            return s.createQuery("FROM LeadHibernate WHERE phone = :phone AND deletedAt IS NULL", LeadHibernate.class)
                    .setParameter("phone", phone).setMaxResults(1).list()
                    .stream().map(mapper::toDomain).findFirst();
        });
    }

    /** Tìm Lead theo email (chưa xóa mềm). @param email email @return Optional */
    @Override public Optional<Lead> findByEmail(String email) {
        return TxSupport.read(sf, s -> {
            return s.createQuery("FROM LeadHibernate WHERE email = :email AND deletedAt IS NULL", LeadHibernate.class)
                    .setParameter("email", email).setMaxResults(1).list()
                    .stream().map(mapper::toDomain).findFirst();
        });
    }

    /** Tìm Lead theo mã (chưa xóa mềm) — web tracking. @param code mã @return Optional */
    @Override public Optional<Lead> findByCode(String code) {
        return TxSupport.read(sf, s -> {
            return s.createQuery("FROM LeadHibernate WHERE code = :code AND deletedAt IS NULL", LeadHibernate.class)
                    .setParameter("code", code).setMaxResults(1).list()
                    .stream().map(mapper::toDomain).findFirst();
        });
    }

    /** Bàn giao toàn bộ Lead của fromUserId sang toUserId. @param fromUserId @param toUserId */
    @Override public void handoverAll(Long fromUserId, Long toUserId) {
        TxSupport.writeVoid(sf, s -> {
            s.createNativeQuery("UPDATE leads SET owner_id = :toUserId WHERE owner_id = :fromUserId AND deleted_at IS NULL")
                    .setParameter("toUserId", toUserId).setParameter("fromUserId", fromUserId).executeUpdate();
            });
    }

    /** Bàn giao hàng loạt Lead sang owner mới. @param ids IDs @param toUserId người nhận @param currentUserId người thực hiện @param isAdminOrManager quyền admin/manager */
    @Override public void handoverBulk(List<Long> ids, Long toUserId, Long currentUserId, boolean isAdminOrManager) {
        if (ids == null || ids.isEmpty()) return;
        TxSupport.writeVoid(sf, s -> {
            String ownerFilter = isAdminOrManager ? "" : " AND owner_id = :currentUserId";
            String sql = "UPDATE leads SET owner_id = :toUserId WHERE id IN (:ids) AND deleted_at IS NULL" + ownerFilter;
            var q = s.createNativeQuery(sql).setParameter("toUserId", toUserId).setParameter("ids", ids);
            if (!isAdminOrManager) q.setParameter("currentUserId", currentUserId);
            q.executeUpdate();
            });
    }

    /** Lấy danh sách Lead chưa xóa có phân trang, lọc owner/trạng thái + tìm kiếm server-side. @param r phân trang @return PageResult */
    @Override public PageResult<Lead> findAll(PageRequest r) {
        return TxSupport.read(sf, s -> {
            String yearFilter = r.getDataAccessFromYear() != null ? " AND YEAR(createdAt) >= :fromYear" : "";
            String ownerFilter = r.getOwnerId() != null ? " AND ownerId = :ownerId" : "";
            String searchFilter = ListQueryUtils.likeClause(r.getQ(), "code", "name", "companyName", "phone", "email");
            var statusVal = ListQueryUtils.parseEnum(vn.com.be_crm.domain.lead.enums.LeadStatus.class, r.getStatus());
            String statusFilter = statusVal != null ? " AND status = :status" : "";
            String where = " WHERE deletedAt IS NULL" + yearFilter + ownerFilter + searchFilter + statusFilter;
            String orderBy = " ORDER BY " + ListQueryUtils.safeSortBy(r.getSortBy(), "createdAt") + " " + ListQueryUtils.safeSortDir(r.getSortDir());
            var q = s.createQuery("FROM LeadHibernate" + where + orderBy, LeadHibernate.class)
                    .setFirstResult(r.getOffset()).setMaxResults(r.getSize());
            var cq = s.createQuery("SELECT COUNT(*) FROM LeadHibernate" + where, Long.class);
            for (var query : List.of(q, cq)) {
                if (r.getDataAccessFromYear() != null) query.setParameter("fromYear", r.getDataAccessFromYear());
                if (r.getOwnerId() != null) query.setParameter("ownerId", r.getOwnerId());
                if (!searchFilter.isEmpty()) query.setParameter("q", ListQueryUtils.likeParam(r.getQ()));
                if (statusVal != null) query.setParameter("status", statusVal);
            }
            List<Lead> items = q.list().stream().map(mapper::toDomain).collect(Collectors.toList());
            long total = cq.uniqueResult();
            return PageResult.<Lead>builder().items(items).total(total).page(r.getPage()).size(r.getSize()).build();
        });
    }
}
