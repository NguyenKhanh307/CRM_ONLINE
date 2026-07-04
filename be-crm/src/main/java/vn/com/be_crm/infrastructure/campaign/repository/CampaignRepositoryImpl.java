package vn.com.be_crm.infrastructure.campaign.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.application.campaign.dto.CampaignStatsResult;
import vn.com.be_crm.application.shared.dto.DeletedItemResult;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.campaign.entity.Campaign;
import vn.com.be_crm.domain.campaign.repository.ICampaignRepository;
import vn.com.be_crm.infrastructure.campaign.entity.CampaignHibernate;
import vn.com.be_crm.infrastructure.campaign.mapper.CampaignHibernateMapper;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Hibernate implementation của ICampaignRepository.
 */
@Repository
public class CampaignRepositoryImpl implements ICampaignRepository {
    private final SessionFactory sf;
    private final CampaignHibernateMapper mapper;

    /** @param sf Hibernate SessionFactory @param mapper mapper */
    public CampaignRepositoryImpl(SessionFactory sf, CampaignHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /** Lưu mới hoặc cập nhật Campaign. @param c @return entity sau khi lưu */
    @Override public Campaign save(Campaign c) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            CampaignHibernate m = s.merge(mapper.toHibernate(c));
            tx.commit(); return mapper.toDomain(m);
        }
    }

    /** Tìm Campaign theo mã (chưa xóa mềm). @param code @return Optional */
    @Override public Optional<Campaign> findByCode(String code) {
        try (Session s = sf.openSession()) {
            return s.createQuery("FROM CampaignHibernate WHERE code = :code AND deletedAt IS NULL", CampaignHibernate.class)
                    .setParameter("code", code).setMaxResults(1).list()
                    .stream().map(mapper::toDomain).findFirst();
        }
    }

    /** Tìm Campaign theo ID — chỉ trả về nếu chưa xóa mềm. @param id @return Optional */
    @Override public Optional<Campaign> findById(Long id) {
        try (Session s = sf.openSession()) {
            CampaignHibernate h = s.find(CampaignHibernate.class, id);
            if (h == null || h.getDeletedAt() != null) return Optional.empty();
            return Optional.of(mapper.toDomain(h));
        }
    }

    /** Xóa mềm Campaign, ghi nhận người xóa. @param id ID @param deletedBy userId người xóa */
    @Override public void deleteById(Long id, Long deletedBy) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            CampaignHibernate h = s.find(CampaignHibernate.class, id);
            if (h != null) { h.setDeletedAt(LocalDateTime.now()); h.setDeletedBy(deletedBy); s.merge(h); }
            tx.commit();
        }
    }

    /** Lấy danh sách Campaign trong thùng rác (30 ngày). @param userId ID người dùng @param isAdmin admin thấy tất cả @param req phân trang */
    @Override public PageResult<DeletedItemResult> findDeleted(Long userId, boolean isAdmin, PageRequest req) {
        try (Session s = sf.openSession()) {
            LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
            String userFilter = isAdmin ? "" : " AND o.deleted_by = :userId";
            String sql = "SELECT o.id, o.code, o.deleted_at, u.full_name FROM campaigns o" +
                    " LEFT JOIN users u ON u.id = o.deleted_by" +
                    " WHERE o.deleted_at IS NOT NULL AND o.deleted_at >= :cutoff AND o.is_purged = 0" +
                    userFilter + " ORDER BY o.deleted_at DESC";
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
            String countSql = "SELECT COUNT(*) FROM campaigns o WHERE o.deleted_at IS NOT NULL AND o.deleted_at >= :cutoff AND o.is_purged = 0" + userFilter;
            var cq = s.createNativeQuery(countSql, Object.class).setParameter("cutoff", cutoff);
            if (!isAdmin) cq.setParameter("userId", userId);
            long total = ((Number) cq.uniqueResult()).longValue();
            return PageResult.<DeletedItemResult>builder().items(items).total(total).page(req.getPage()).size(req.getSize()).build();
        }
    }

    /** Khôi phục Campaign từ thùng rác. @param id ID */
    @Override public void restoreById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            CampaignHibernate h = s.find(CampaignHibernate.class, id);
            if (h != null) { h.setDeletedAt(null); h.setDeletedBy(null); h.setPurged(false); s.merge(h); }
            tx.commit();
        }
    }

    /** Ẩn Campaign khỏi thùng rác (is_purged = true). @param id ID */
    @Override public void purgeById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            CampaignHibernate h = s.find(CampaignHibernate.class, id);
            if (h != null) { h.setPurged(true); s.merge(h); }
            tx.commit();
        }
    }

    /** Bàn giao toàn bộ Campaign của fromUserId sang toUserId. @param fromUserId @param toUserId */
    @Override public void handoverAll(Long fromUserId, Long toUserId) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            s.createNativeQuery("UPDATE campaigns SET owner_id = :toUserId WHERE owner_id = :fromUserId AND deleted_at IS NULL")
                    .setParameter("toUserId", toUserId).setParameter("fromUserId", fromUserId).executeUpdate();
            tx.commit();
        }
    }

    /** Bàn giao hàng loạt Campaign sang owner mới. @param ids IDs @param toUserId người nhận @param currentUserId người thực hiện @param isAdminOrManager quyền */
    @Override public void handoverBulk(List<Long> ids, Long toUserId, Long currentUserId, boolean isAdminOrManager) {
        if (ids == null || ids.isEmpty()) return;
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            String ownerFilter = isAdminOrManager ? "" : " AND owner_id = :currentUserId";
            String sql = "UPDATE campaigns SET owner_id = :toUserId WHERE id IN (:ids) AND deleted_at IS NULL" + ownerFilter;
            var q = s.createNativeQuery(sql).setParameter("toUserId", toUserId).setParameter("ids", ids);
            if (!isAdminOrManager) q.setParameter("currentUserId", currentUserId);
            q.executeUpdate();
            tx.commit();
        }
    }

    /** Lấy danh sách Campaign chưa xóa có phân trang. @param r @return PageResult */
    @Override public PageResult<Campaign> findAll(PageRequest r) {
        try (Session s = sf.openSession()) {
            String yearFilter = r.getDataAccessFromYear() != null ? " AND YEAR(createdAt) >= :fromYear" : "";
            var q = s.createQuery("FROM CampaignHibernate WHERE deletedAt IS NULL" + yearFilter + " ORDER BY " + r.getSortBy() + " " + r.getSortDir(), CampaignHibernate.class)
                    .setFirstResult(r.getOffset()).setMaxResults(r.getSize());
            if (r.getDataAccessFromYear() != null) q.setParameter("fromYear", r.getDataAccessFromYear());
            List<Campaign> items = q.list().stream().map(mapper::toDomain).collect(Collectors.toList());
            var cq = s.createQuery("SELECT COUNT(o) FROM CampaignHibernate o WHERE o.deletedAt IS NULL" + (r.getDataAccessFromYear() != null ? " AND YEAR(o.createdAt) >= :fromYear" : ""), Long.class);
            if (r.getDataAccessFromYear() != null) cq.setParameter("fromYear", r.getDataAccessFromYear());
            long total = cq.uniqueResult();
            return PageResult.<Campaign>builder().items(items).total(total).page(r.getPage()).size(r.getSize()).build();
        }
    }

    /** Tính chỉ số ROI của chiến dịch qua native query đếm trên các bảng liên quan. @param campaignId @return CampaignStatsResult */
    @Override public CampaignStatsResult getStats(Long campaignId) {
        try (Session s = sf.openSession()) {
            long memberCount = count(s, "SELECT COUNT(*) FROM campaign_members WHERE campaign_id = :id", campaignId);
            long sentCount = count(s, "SELECT COUNT(*) FROM campaign_members WHERE campaign_id = :id AND status IN ('sent','opened','clicked','responded')", campaignId);
            long leadCount = count(s, "SELECT COUNT(*) FROM leads WHERE campaign_id = :id AND deleted_at IS NULL", campaignId);
            long oppCount = count(s, "SELECT COUNT(*) FROM opportunities WHERE campaign_id = :id AND deleted_at IS NULL", campaignId);
            long wonOppCount = count(s, "SELECT COUNT(*) FROM opportunities WHERE campaign_id = :id AND status = 'won' AND deleted_at IS NULL", campaignId);
            long orderCount = count(s, "SELECT COUNT(*) FROM orders WHERE campaign_id = :id AND deleted_at IS NULL", campaignId);
            Object revObj = s.createNativeQuery("SELECT COALESCE(SUM(total),0) FROM orders WHERE campaign_id = :id AND status <> 'cancelled' AND deleted_at IS NULL", Object.class)
                    .setParameter("id", campaignId).uniqueResult();
            BigDecimal revenue = revObj instanceof BigDecimal bd ? bd : new BigDecimal(revObj.toString());
            CampaignHibernate c = s.find(CampaignHibernate.class, campaignId);
            BigDecimal actualCost = c != null ? c.getActualCost() : null;
            return CampaignStatsResult.builder()
                    .campaignId(campaignId).memberCount(memberCount).sentCount(sentCount)
                    .leadCount(leadCount).opportunityCount(oppCount).wonOpportunityCount(wonOppCount)
                    .orderCount(orderCount).revenue(revenue).actualCost(actualCost).build();
        }
    }

    /** Chạy native count query với tham số id. */
    private long count(Session s, String sql, Long id) {
        return ((Number) s.createNativeQuery(sql, Object.class).setParameter("id", id).uniqueResult()).longValue();
    }
}
