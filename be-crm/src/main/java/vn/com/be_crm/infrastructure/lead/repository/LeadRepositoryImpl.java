package vn.com.be_crm.infrastructure.lead.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.core.dto.delete.DeletedItemResult;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.page.PageResult;
import vn.com.be_crm.domain.lead.entity.Lead;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;
import vn.com.be_crm.core.audit.CurrentUserHolder;
import vn.com.be_crm.infrastructure.lead.entity.LeadHibernate;
import vn.com.be_crm.infrastructure.lead.mapper.LeadHibernateMapper;
import vn.com.be_crm.core.util.ListQueryUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import vn.com.be_crm.core.tx.impl.TxSupport;

// impl Hibernate của ILeadRepository — xóa mềm: deleteById set deleted_at = now()
@Repository
public class LeadRepositoryImpl implements ILeadRepository {
    private final SessionFactory sf;
    private final LeadHibernateMapper mapper;

    public LeadRepositoryImpl(SessionFactory sf, LeadHibernateMapper mapper) {
        this.sf = sf;
        this.mapper = mapper;
    }

    @Override
    public Lead save(Lead l) {
        return TxSupport.write(sf, s -> {
            LeadHibernate m = s.merge(mapper.toHibernate(l));
            return mapper.toDomain(m);
        });
    }

    @Override
    public Optional<Lead> findById(Long id) {
        return TxSupport.read(sf, s -> {
            LeadHibernate h = s.find(LeadHibernate.class, id);
            if (h == null || h.getDeletedAt() != null)
                return Optional.empty();
            return Optional.of(mapper.toDomain(h));
        });
    }

    @Override
    public void deleteById(Long id, Long deletedBy) {
        TxSupport.writeVoid(sf, s -> {
            LeadHibernate h = s.find(LeadHibernate.class, id);
            if (h != null) {
                h.setDeletedAt(LocalDateTime.now());
                h.setDeletedBy(deletedBy);
                s.merge(h);
            }
        });
    }

    // thùng rác — chỉ bản ghi xóa mềm trong 30 ngày gần nhất, isAdmin=false thì chỉ
    // của chính mình
    @Override
    public PageResult<DeletedItemResult> findDeleted(Long userId, boolean isAdmin, PageRequest req) {
        return TxSupport.read(sf, s -> {
            // chỉ lấy bản ghi xóa mềm trong 30 ngày gần nhất
            LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
            String userFilter = isAdmin ? "" : " AND l.deleted_by = :userId";
            // LEFT JOIN users để lấy tên người xóa
            String sql = "SELECT l.id, l.name, l.deleted_at, u.full_name FROM leads l" +
                    " LEFT JOIN users u ON u.id = l.deleted_by" +
                    " WHERE l.deleted_at IS NOT NULL AND l.deleted_at >= :cutoff AND l.is_purged = 0" +
                    userFilter + " ORDER BY l.deleted_at DESC";
            // map Object[] -> DeletedItemResult (TiDB trả java.sql.Timestamp cho cột
            // DATETIME)
            var q = s.createNativeQuery(sql, Object[].class)
                    .setParameter("cutoff", cutoff)
                    .setFirstResult(req.getOffset()).setMaxResults(req.getSize());
            if (!isAdmin)
                q.setParameter("userId", userId);
            List<DeletedItemResult> items = q.list().stream()
                    .map(row -> new DeletedItemResult(
                            ((Number) row[0]).longValue(), (String) row[1],
                            row[2] instanceof Timestamp ts ? ts.toLocalDateTime() : (LocalDateTime) row[2],
                            (String) row[3]))
                    .collect(Collectors.toList());
            String countSql = "SELECT COUNT(*) FROM leads l WHERE l.deleted_at IS NOT NULL AND l.deleted_at >= :cutoff AND l.is_purged = 0"
                    + userFilter;
            var cq = s.createNativeQuery(countSql, Object.class).setParameter("cutoff", cutoff);
            if (!isAdmin)
                cq.setParameter("userId", userId);
            long total = ((Number) cq.uniqueResult()).longValue();
            return PageResult.<DeletedItemResult>builder().items(items).total(total).page(req.getPage())
                    .size(req.getSize()).build();
        });
    }

    @Override
    public void restoreById(Long id) {
        TxSupport.writeVoid(sf, s -> {
            LeadHibernate h = s.find(LeadHibernate.class, id);
            if (h != null) {
                h.setDeletedAt(null);
                h.setDeletedBy(null);
                h.setPurged(false);
                s.merge(h);
            }
        });
    }

    @Override
    public void purgeById(Long id) {
        TxSupport.writeVoid(sf, s -> {
            LeadHibernate h = s.find(LeadHibernate.class, id);
            if (h != null) {
                h.setPurged(true);
                s.merge(h);
            }
        });
    }

    @Override
    public Optional<Lead> findByPhone(String phone) {
        return TxSupport.read(sf, s -> {
            return s.createQuery("FROM LeadHibernate WHERE phone = :phone AND deletedAt IS NULL", LeadHibernate.class)
                    .setParameter("phone", phone).setMaxResults(1).list()
                    .stream().map(mapper::toDomain).findFirst();
        });
    }

    @Override
    public Optional<Lead> findByEmail(String email) {
        return TxSupport.read(sf, s -> {
            return s.createQuery("FROM LeadHibernate WHERE email = :email AND deletedAt IS NULL", LeadHibernate.class)
                    .setParameter("email", email).setMaxResults(1).list()
                    .stream().map(mapper::toDomain).findFirst();
        });
    }

    @Override
    public Optional<Lead> findByCode(String code) {
        return TxSupport.read(sf, s -> {
            return s.createQuery("FROM LeadHibernate WHERE code = :code AND deletedAt IS NULL", LeadHibernate.class)
                    .setParameter("code", code).setMaxResults(1).list()
                    .stream().map(mapper::toDomain).findFirst();
        });
    }

    // native SQL bypass Hibernate persistence context -> phải tự ghi
    // updated_by/updated_at
    @Override
    public void handoverAll(Long fromUserId, Long toUserId) {
        TxSupport.writeVoid(sf, s -> {
            s.createNativeQuery(
                    "UPDATE leads SET owner_id = :toUserId, updated_by = :actor, updated_at = NOW() WHERE owner_id = :fromUserId AND deleted_at IS NULL")
                    .setParameter("toUserId", toUserId).setParameter("fromUserId", fromUserId)
                    .setParameter("actor", CurrentUserHolder.get()).executeUpdate();
        });
    }

    // isAdminOrManager=false thì chỉ bàn giao được bản ghi currentUserId đang là
    // owner
    @Override
    public void handoverBulk(List<Long> ids, Long toUserId, Long currentUserId, boolean isAdminOrManager) {
        if (ids == null || ids.isEmpty())
            return;
        TxSupport.writeVoid(sf, s -> {
            String ownerFilter = isAdminOrManager ? "" : " AND owner_id = :currentUserId";
            String sql = "UPDATE leads SET owner_id = :toUserId, updated_by = :actor, updated_at = NOW() WHERE id IN (:ids) AND deleted_at IS NULL"
                    + ownerFilter;
            var q = s.createNativeQuery(sql).setParameter("toUserId", toUserId).setParameter("ids", ids)
                    .setParameter("actor", currentUserId);
            if (!isAdminOrManager)
                q.setParameter("currentUserId", currentUserId);
            q.executeUpdate();
        });
    }

    // tìm tiềm năng đã liên kết cơ hội nguồn của một báo giá (quotation ->
    // opportunity ->
    // leads.converted_opportunity_id) — dùng để phát hiện "báo giá/đơn hàng này bắt
    // nguồn từ tiềm năng nào"
    @Override
    public Optional<Lead> findByQuotationId(Long quotationId) {
        return TxSupport.read(sf, s -> {
            String sql = "SELECT l.id FROM leads l " +
                    "JOIN quotations q ON q.opportunity_id = l.converted_opportunity_id " +
                    "WHERE q.id = :quotationId AND l.deleted_at IS NULL";
            List<Object> rows = s.createNativeQuery(sql, Object.class)
                    .setParameter("quotationId", quotationId).getResultList();
            if (rows.isEmpty())
                return Optional.<Lead>empty();
            Long leadId = ((Number) rows.get(0)).longValue();
            LeadHibernate h = s.find(LeadHibernate.class, leadId);
            return h == null || h.getDeletedAt() != null ? Optional.<Lead>empty() : Optional.of(mapper.toDomain(h));
        });
    }

    @Override
    public boolean hasAnyOrder(Long leadId, Long excludeOrderId) {
        return TxSupport.read(sf, s -> {
            String sql = "SELECT COUNT(*) FROM orders o " +
                    "JOIN quotations q ON q.id = o.quotation_id " +
                    "JOIN leads l ON l.converted_opportunity_id = q.opportunity_id " +
                    "WHERE l.id = :leadId AND o.deleted_at IS NULL" +
                    (excludeOrderId != null ? " AND o.id != :excludeOrderId" : "");
            var query = s.createNativeQuery(sql, Object.class).setParameter("leadId", leadId);
            if (excludeOrderId != null)
                query.setParameter("excludeOrderId", excludeOrderId);
            long count = ((Number) query.uniqueResult()).longValue();
            return count > 0;
        });
    }

    @Override
    public PageResult<Lead> findAll(PageRequest r) {
        return TxSupport.read(sf, s -> {
            String yearFilter = r.getDataAccessFromYear() != null ? " AND YEAR(createdAt) >= :fromYear" : "";
            String ownerFilter = r.getOwnerId() != null
                    ? (r.isIncludeUnassigned() ? " AND (ownerId = :ownerId OR ownerId IS NULL)"
                            : " AND ownerId = :ownerId")
                    : "";
            String searchFilter = ListQueryUtils.likeClause(r.getQ(), "code", "name", "companyName", "phone", "email");
            var statusVal = ListQueryUtils.parseEnum(vn.com.be_crm.domain.lead.enums.LeadStatus.class, r.getStatus());
            String statusFilter = statusVal != null ? " AND status = :status" : "";
            String where = " WHERE deletedAt IS NULL" + yearFilter + ownerFilter + searchFilter + statusFilter;
            String orderBy = " ORDER BY " + ListQueryUtils.safeSortBy(r.getSortBy(), "createdAt") + " "
                    + ListQueryUtils.safeSortDir(r.getSortDir());
            var q = s.createQuery("FROM LeadHibernate" + where + orderBy, LeadHibernate.class)
                    .setFirstResult(r.getOffset()).setMaxResults(r.getSize());
            var cq = s.createQuery("SELECT COUNT(*) FROM LeadHibernate" + where, Long.class);
            for (var query : List.of(q, cq)) {
                if (r.getDataAccessFromYear() != null)
                    query.setParameter("fromYear", r.getDataAccessFromYear());
                if (r.getOwnerId() != null)
                    query.setParameter("ownerId", r.getOwnerId());
                if (!searchFilter.isEmpty())
                    query.setParameter("q", ListQueryUtils.likeParam(r.getQ()));
                if (statusVal != null)
                    query.setParameter("status", statusVal);
            }
            List<Lead> items = q.list().stream().map(mapper::toDomain).collect(Collectors.toList());
            long total = cq.uniqueResult();
            return PageResult.<Lead>builder().items(items).total(total).page(r.getPage()).size(r.getSize()).build();
        });
    }
}
