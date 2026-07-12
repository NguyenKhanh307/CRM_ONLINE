package vn.com.be_crm.infrastructure.quotation.repository;

import vn.com.be_crm.infrastructure.shared.audit.CurrentUserHolder;
import vn.com.be_crm.infrastructure.shared.util.ListQueryUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.application.shared.dto.DeletedItemResult;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.entity.QuotationItem;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;
import vn.com.be_crm.infrastructure.quotation.entity.QuotationHibernate;
import vn.com.be_crm.infrastructure.quotation.entity.QuotationItemHibernate;
import vn.com.be_crm.infrastructure.quotation.mapper.QuotationHibernateMapper;
import vn.com.be_crm.infrastructure.quotation.mapper.QuotationItemHibernateMapper;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import vn.com.be_crm.infrastructure.shared.tx.TxSupport;

/**
 * Hibernate implementation của IQuotationRepository.
 */
@Repository
public class QuotationRepositoryImpl implements IQuotationRepository {
    private final SessionFactory sf;
    private final QuotationHibernateMapper mapper;
    private final QuotationItemHibernateMapper itemMapper;

    /** @param sf Hibernate SessionFactory @param mapper mapper @param itemMapper mapper dòng hàng */
    public QuotationRepositoryImpl(SessionFactory sf, QuotationHibernateMapper mapper,
                                   QuotationItemHibernateMapper itemMapper) {
        this.sf = sf; this.mapper = mapper; this.itemMapper = itemMapper;
    }

    /** Lưu mới hoặc cập nhật Quotation. @param q @return entity sau khi lưu */
    @Override public Quotation save(Quotation q) {
        return TxSupport.write(sf, s -> {
            QuotationHibernate m = s.merge(mapper.toHibernate(q));
            return mapper.toDomain(m);
        });
    }

    /** Tìm Quotation theo mã (chưa xóa mềm). @param code mã @return Optional */
    @Override public Optional<Quotation> findByCode(String code) {
        return TxSupport.read(sf, s -> {
            return s.createQuery("FROM QuotationHibernate WHERE code = :code AND deletedAt IS NULL", QuotationHibernate.class)
                    .setParameter("code", code).setMaxResults(1).list()
                    .stream().map(mapper::toDomain).findFirst();
        });
    }

    /** Tìm Quotation theo token phản hồi công khai (chưa xóa mềm). @param token token @return Optional */
    @Override public Optional<Quotation> findByResponseToken(String token) {
        return TxSupport.read(sf, s -> {
            return s.createQuery("FROM QuotationHibernate WHERE responseToken = :token AND deletedAt IS NULL", QuotationHibernate.class)
                    .setParameter("token", token).setMaxResults(1).list()
                    .stream().map(mapper::toDomain).findFirst();
        });
    }

    /** Lấy danh sách báo giá theo cơ hội (chưa xóa mềm). @param opportunityId @return danh sách */
    @Override public List<Quotation> findAllByOpportunityId(Long opportunityId) {
        return TxSupport.read(sf, s -> {
            return s.createQuery("FROM QuotationHibernate WHERE opportunityId = :oid AND deletedAt IS NULL", QuotationHibernate.class)
                    .setParameter("oid", opportunityId).list()
                    .stream().map(mapper::toDomain).collect(Collectors.toList());
        });
    }

    /**
     * Lưu Quotation kèm danh sách dòng hàng trong MỘT transaction.
     * @param q     domain entity báo giá
     * @param items danh sách dòng hàng
     * @return báo giá sau khi lưu
     */
    @Override public Quotation saveWithItems(Quotation q, List<QuotationItem> items) {
        return TxSupport.write(sf, s -> {
            // Lưu header báo giá trước để lấy ID
            QuotationHibernate m = s.merge(mapper.toHibernate(q));
            // Gán quotationId vừa có cho từng dòng hàng rồi lưu trong cùng transaction
            for (QuotationItem item : items) {
                QuotationItemHibernate ih = itemMapper.toHibernate(item);
                ih.setQuotationId(m.getId());
                s.merge(ih);
            }
            // Commit và trả về domain entity đã lưu
            return mapper.toDomain(m);
        });
    }

    /** Tìm Quotation theo ID — chỉ trả về nếu chưa xóa mềm. @param id @return Optional */
    @Override public Optional<Quotation> findById(Long id) {
        return TxSupport.read(sf, s -> {
            QuotationHibernate h = s.find(QuotationHibernate.class, id);
            if (h == null || h.getDeletedAt() != null) return Optional.empty();
            return Optional.of(mapper.toDomain(h));
        });
    }

    /** Xóa mềm Quotation, ghi nhận người xóa. @param id ID @param deletedBy userId người xóa */
    @Override public void deleteById(Long id, Long deletedBy) {
        TxSupport.writeVoid(sf, s -> {
            QuotationHibernate h = s.find(QuotationHibernate.class, id);
            if (h != null) { h.setDeletedAt(LocalDateTime.now()); h.setDeletedBy(deletedBy); s.merge(h); }
            });
    }

    /** Lấy danh sách Quotation trong thùng rác (30 ngày). @param userId ID người dùng @param isAdmin admin thấy tất cả @param req phân trang */
    @Override public PageResult<DeletedItemResult> findDeleted(Long userId, boolean isAdmin, PageRequest req) {
        return TxSupport.read(sf, s -> {
            // Mốc 30 ngày: chỉ hiện bản ghi đã xóa trong 30 ngày gần đây
            LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
            // Không phải admin → chỉ xem bản ghi do chính mình xóa
            String userFilter = isAdmin ? "" : " AND q.deleted_by = :userId";
            // Native query LEFT JOIN users để lấy tên người xóa
            String sql = "SELECT q.id, q.code, q.deleted_at, u.full_name FROM quotations q" +
                    " LEFT JOIN users u ON u.id = q.deleted_by" +
                    " WHERE q.deleted_at IS NOT NULL AND q.deleted_at >= :cutoff AND q.is_purged = 0" +
                    userFilter + " ORDER BY q.deleted_at DESC";
            var qr = s.createNativeQuery(sql, Object[].class)
                    .setParameter("cutoff", cutoff)
                    .setFirstResult(req.getOffset()).setMaxResults(req.getSize());
            if (!isAdmin) qr.setParameter("userId", userId);
            List<DeletedItemResult> items = qr.list().stream()
                    .map(row -> new DeletedItemResult(
                            ((Number) row[0]).longValue(), (String) row[1],
                            row[2] instanceof Timestamp ts ? ts.toLocalDateTime() : (LocalDateTime) row[2],
                            (String) row[3]))
                    .collect(Collectors.toList());
            // Query đếm tổng số bản ghi đã xóa để phân trang
            String countSql = "SELECT COUNT(*) FROM quotations q WHERE q.deleted_at IS NOT NULL AND q.deleted_at >= :cutoff AND q.is_purged = 0" + userFilter;
            var cq = s.createNativeQuery(countSql, Object.class).setParameter("cutoff", cutoff);
            if (!isAdmin) cq.setParameter("userId", userId);
            long total = ((Number) cq.uniqueResult()).longValue();
            return PageResult.<DeletedItemResult>builder().items(items).total(total).page(req.getPage()).size(req.getSize()).build();
        });
    }

    /** Khôi phục Quotation từ thùng rác. @param id ID */
    @Override public void restoreById(Long id) {
        TxSupport.writeVoid(sf, s -> {
            QuotationHibernate h = s.find(QuotationHibernate.class, id);
            if (h != null) { h.setDeletedAt(null); h.setDeletedBy(null); h.setPurged(false); s.merge(h); }
            });
    }

    /** Ẩn Quotation khỏi thùng rác (is_purged = true). @param id ID */
    @Override public void purgeById(Long id) {
        TxSupport.writeVoid(sf, s -> {
            QuotationHibernate h = s.find(QuotationHibernate.class, id);
            if (h != null) { h.setPurged(true); s.merge(h); }
            });
    }

    /** Bàn giao toàn bộ Quotation của fromUserId sang toUserId. @param fromUserId @param toUserId */
    @Override public void handoverAll(Long fromUserId, Long toUserId) {
        TxSupport.writeVoid(sf, s -> {
            // Bàn giao đi bằng native SQL (bypass Hibernate) → phải tự ghi updated_by/updated_at
            s.createNativeQuery("UPDATE quotations SET owner_id = :toUserId, updated_by = :actor, updated_at = NOW() WHERE owner_id = :fromUserId AND deleted_at IS NULL")
                    .setParameter("toUserId", toUserId).setParameter("fromUserId", fromUserId)
                    .setParameter("actor", CurrentUserHolder.get()).executeUpdate();
            });
    }

    /** Bàn giao hàng loạt Quotation sang owner mới. @param ids IDs @param toUserId người nhận @param currentUserId người thực hiện @param isAdminOrManager quyền admin/manager */
    @Override public void handoverBulk(List<Long> ids, Long toUserId, Long currentUserId, boolean isAdminOrManager) {
        if (ids == null || ids.isEmpty()) return;
        TxSupport.writeVoid(sf, s -> {
            String ownerFilter = isAdminOrManager ? "" : " AND owner_id = :currentUserId";
            String sql = "UPDATE quotations SET owner_id = :toUserId, updated_by = :actor, updated_at = NOW() WHERE id IN (:ids) AND deleted_at IS NULL" + ownerFilter;
            var q = s.createNativeQuery(sql).setParameter("toUserId", toUserId).setParameter("ids", ids)
                    .setParameter("actor", currentUserId);
            if (!isAdminOrManager) q.setParameter("currentUserId", currentUserId);
            q.executeUpdate();
            });
    }

    /** Lấy danh sách Quotation chưa xóa có phân trang. @param r @return PageResult */
    @Override public PageResult<Quotation> findAll(PageRequest r) {
        return TxSupport.read(sf, s -> {
            String yearFilter = r.getDataAccessFromYear() != null ? " AND YEAR(createdAt) >= :fromYear" : "";
            String ownerFilter = r.getOwnerId() != null ? " AND ownerId = :ownerId" : "";
            String searchFilter = ListQueryUtils.likeClause(r.getQ(), "code");
            var statusVal = ListQueryUtils.parseEnum(vn.com.be_crm.domain.quotation.enums.QuotationStatus.class, r.getStatus());
            String statusFilter = statusVal != null ? " AND status = :status" : "";
            String where = " WHERE deletedAt IS NULL" + yearFilter + ownerFilter + searchFilter + statusFilter;
            String orderBy = " ORDER BY " + ListQueryUtils.safeSortBy(r.getSortBy(), "createdAt") + " " + ListQueryUtils.safeSortDir(r.getSortDir());
            var q = s.createQuery("FROM QuotationHibernate" + where + orderBy, QuotationHibernate.class)
                    .setFirstResult(r.getOffset()).setMaxResults(r.getSize());
            var cq = s.createQuery("SELECT COUNT(*) FROM QuotationHibernate" + where, Long.class);
            for (var query : List.of(q, cq)) {
                if (r.getDataAccessFromYear() != null) query.setParameter("fromYear", r.getDataAccessFromYear());
                if (r.getOwnerId() != null) query.setParameter("ownerId", r.getOwnerId());
                if (!searchFilter.isEmpty()) query.setParameter("q", ListQueryUtils.likeParam(r.getQ()));
                if (statusVal != null) query.setParameter("status", statusVal);
            }
            List<Quotation> items = q.list().stream().map(mapper::toDomain).collect(Collectors.toList());
            long total = cq.uniqueResult();
            return PageResult.<Quotation>builder().items(items).total(total).page(r.getPage()).size(r.getSize()).build();
        });
    }
}
