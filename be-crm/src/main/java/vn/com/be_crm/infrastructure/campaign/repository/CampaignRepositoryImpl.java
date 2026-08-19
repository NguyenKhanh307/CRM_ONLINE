package vn.com.be_crm.infrastructure.campaign.repository;

import vn.com.be_crm.core.audit.CurrentUserHolder;
import vn.com.be_crm.core.util.ListQueryUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.application.campaign.dto.CampaignStatsResult;
import vn.com.be_crm.core.dto.delete.DeletedItemResult;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.page.PageResult;
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
import vn.com.be_crm.core.tx.impl.TxSupport;

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
        return TxSupport.write(sf, s -> {
            CampaignHibernate m = s.merge(mapper.toHibernate(c));
            return mapper.toDomain(m);
        });
    }

    /** Tìm Campaign theo mã (chưa xóa mềm). @param code @return Optional */
    @Override public Optional<Campaign> findByCode(String code) {
        return TxSupport.read(sf, s -> {
            return s.createQuery("FROM CampaignHibernate WHERE code = :code AND deletedAt IS NULL", CampaignHibernate.class)
                    .setParameter("code", code).setMaxResults(1).list()
                    .stream().map(mapper::toDomain).findFirst();
        });
    }

    /** Tìm Campaign theo ID — chỉ trả về nếu chưa xóa mềm. @param id @return Optional */
    @Override public Optional<Campaign> findById(Long id) {
        return TxSupport.read(sf, s -> {
            CampaignHibernate h = s.find(CampaignHibernate.class, id);
            if (h == null || h.getDeletedAt() != null) return Optional.empty();
            return Optional.of(mapper.toDomain(h));
        });
    }

    /** Xóa mềm Campaign, ghi nhận người xóa. @param id ID @param deletedBy userId người xóa */
    @Override public void deleteById(Long id, Long deletedBy) {
        TxSupport.writeVoid(sf, s -> {
            CampaignHibernate h = s.find(CampaignHibernate.class, id);
            if (h != null) { h.setDeletedAt(LocalDateTime.now()); h.setDeletedBy(deletedBy); s.merge(h); }
            });
    }

    /** Lấy danh sách Campaign trong thùng rác (30 ngày). @param userId ID người dùng @param isAdmin admin thấy tất cả @param req phân trang */
    @Override public PageResult<DeletedItemResult> findDeleted(Long userId, boolean isAdmin, PageRequest req) {
        return TxSupport.read(sf, s -> {
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
        });
    }

    /** Khôi phục Campaign từ thùng rác. @param id ID */
    @Override public void restoreById(Long id) {
        TxSupport.writeVoid(sf, s -> {
            CampaignHibernate h = s.find(CampaignHibernate.class, id);
            if (h != null) { h.setDeletedAt(null); h.setDeletedBy(null); h.setPurged(false); s.merge(h); }
            });
    }

    /** Ẩn Campaign khỏi thùng rác (is_purged = true). @param id ID */
    @Override public void purgeById(Long id) {
        TxSupport.writeVoid(sf, s -> {
            CampaignHibernate h = s.find(CampaignHibernate.class, id);
            if (h != null) { h.setPurged(true); s.merge(h); }
            });
    }

    /** Bàn giao toàn bộ Campaign của fromUserId sang toUserId. @param fromUserId @param toUserId */
    @Override public void handoverAll(Long fromUserId, Long toUserId) {
        TxSupport.writeVoid(sf, s -> {
            // Bàn giao đi bằng native SQL (bypass Hibernate) → phải tự ghi updated_by/updated_at
            s.createNativeQuery("UPDATE campaigns SET owner_id = :toUserId, updated_by = :actor, updated_at = NOW() WHERE owner_id = :fromUserId AND deleted_at IS NULL")
                    .setParameter("toUserId", toUserId).setParameter("fromUserId", fromUserId)
                    .setParameter("actor", CurrentUserHolder.get()).executeUpdate();
            });
    }

    /** Bàn giao hàng loạt Campaign sang owner mới. @param ids IDs @param toUserId người nhận @param currentUserId người thực hiện @param isAdminOrManager quyền */
    @Override public void handoverBulk(List<Long> ids, Long toUserId, Long currentUserId, boolean isAdminOrManager) {
        if (ids == null || ids.isEmpty()) return;
        TxSupport.writeVoid(sf, s -> {
            String ownerFilter = isAdminOrManager ? "" : " AND owner_id = :currentUserId";
            String sql = "UPDATE campaigns SET owner_id = :toUserId, updated_by = :actor, updated_at = NOW() WHERE id IN (:ids) AND deleted_at IS NULL" + ownerFilter;
            var q = s.createNativeQuery(sql).setParameter("toUserId", toUserId).setParameter("ids", ids)
                    .setParameter("actor", currentUserId);
            if (!isAdminOrManager) q.setParameter("currentUserId", currentUserId);
            q.executeUpdate();
            });
    }

    /** Lấy danh sách Campaign chưa xóa có phân trang. @param r @return PageResult */
    @Override public PageResult<Campaign> findAll(PageRequest r) {
        return TxSupport.read(sf, s -> {
            String yearFilter = r.getDataAccessFromYear() != null ? " AND YEAR(createdAt) >= :fromYear" : "";
            String ownerFilter = r.getOwnerId() != null ? " AND ownerId = :ownerId" : "";
            String searchFilter = ListQueryUtils.likeClause(r.getQ(), "code", "name");
            var statusVal = ListQueryUtils.parseEnum(vn.com.be_crm.domain.campaign.enums.CampaignStatus.class, r.getStatus());
            String statusFilter = statusVal != null ? " AND status = :status" : "";
            String where = " WHERE deletedAt IS NULL" + yearFilter + ownerFilter + searchFilter + statusFilter;
            String orderBy = " ORDER BY " + ListQueryUtils.safeSortBy(r.getSortBy(), "createdAt") + " " + ListQueryUtils.safeSortDir(r.getSortDir());
            var q = s.createQuery("FROM CampaignHibernate" + where + orderBy, CampaignHibernate.class)
                    .setFirstResult(r.getOffset()).setMaxResults(r.getSize());
            var cq = s.createQuery("SELECT COUNT(*) FROM CampaignHibernate" + where, Long.class);
            for (var query : List.of(q, cq)) {
                if (r.getDataAccessFromYear() != null) query.setParameter("fromYear", r.getDataAccessFromYear());
                if (r.getOwnerId() != null) query.setParameter("ownerId", r.getOwnerId());
                if (!searchFilter.isEmpty()) query.setParameter("q", ListQueryUtils.likeParam(r.getQ()));
                if (statusVal != null) query.setParameter("status", statusVal);
            }
            List<Campaign> items = q.list().stream().map(mapper::toDomain).collect(Collectors.toList());
            long total = cq.uniqueResult();
            return PageResult.<Campaign>builder().items(items).total(total).page(r.getPage()).size(r.getSize()).build();
        });
    }

    /** Tính chỉ số ROI của chiến dịch qua native query đếm trên các bảng liên quan. @param campaignId @return CampaignStatsResult */
    @Override public CampaignStatsResult getStats(Long campaignId) {
        return TxSupport.read(sf, s -> {
            long memberCount = count(s, "SELECT COUNT(*) FROM campaign_members WHERE campaign_id = :id", campaignId);
            long sentCount = count(s, "SELECT COUNT(*) FROM campaign_members WHERE campaign_id = :id AND status IN ('sent','opened','clicked','responded')", campaignId);
            long leadCount = count(s, "SELECT COUNT(*) FROM leads WHERE campaign_id = :id AND deleted_at IS NULL", campaignId);
            long oppCount = count(s, "SELECT COUNT(*) FROM opportunities WHERE campaign_id = :id AND deleted_at IS NULL", campaignId);
            long wonOppCount = count(s, "SELECT COUNT(*) FROM opportunities WHERE campaign_id = :id AND status = 'won' AND deleted_at IS NULL", campaignId);
            // orders/quotations không có cột campaign_id/total riêng — quy kết qua chuỗi
            // orders -> quotations -> opportunities.campaign_id, doanh thu tính từ dòng hàng hóa
            // đơn (giống campaignRevenueSubquery() trong DashboardRepositoryImpl)
            long orderCount = ((Number) s.createNativeQuery(
                    "SELECT COUNT(*) FROM orders o " +
                            "JOIN quotations q ON q.id = o.quotation_id " +
                            "JOIN opportunities opp ON opp.id = q.opportunity_id " +
                            "WHERE opp.campaign_id = :id AND o.deleted_at IS NULL", Object.class)
                    .setParameter("id", campaignId).uniqueResult()).longValue();
            Object revObj = s.createNativeQuery(
                    "SELECT COALESCE(SUM((ii.quantity * ii.unit_price - ii.discount) * (1 + ii.tax_rate / 100)),0) " +
                            "FROM invoices i JOIN invoice_items ii ON ii.invoice_id = i.id " +
                            "JOIN orders o ON o.id = i.order_id " +
                            "JOIN quotations q ON q.id = o.quotation_id " +
                            "JOIN opportunities opp ON opp.id = q.opportunity_id " +
                            "WHERE opp.campaign_id = :id AND i.status <> 'cancelled' AND i.deleted_at IS NULL", Object.class)
                    .setParameter("id", campaignId).uniqueResult();
            BigDecimal revenue = revObj instanceof BigDecimal bd ? bd : new BigDecimal(revObj.toString());
            CampaignHibernate c = s.find(CampaignHibernate.class, campaignId);
            BigDecimal actualCost = c != null ? c.getActualCost() : null;
            BigDecimal expectedRevenue = c != null ? c.getExpectedRevenue() : null;
            return CampaignStatsResult.builder()
                    .campaignId(campaignId).memberCount(memberCount).sentCount(sentCount)
                    .leadCount(leadCount).opportunityCount(oppCount).wonOpportunityCount(wonOppCount)
                    .orderCount(orderCount).revenue(revenue).actualCost(actualCost)
                    .expectedRevenue(expectedRevenue).build();
        });
    }

    /** Chạy native count query với tham số id. */
    private long count(Session s, String sql, Long id) {
        return ((Number) s.createNativeQuery(sql, Object.class).setParameter("id", id).uniqueResult()).longValue();
    }
}
