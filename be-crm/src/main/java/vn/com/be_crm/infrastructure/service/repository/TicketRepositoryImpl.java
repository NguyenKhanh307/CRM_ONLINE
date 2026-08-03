package vn.com.be_crm.infrastructure.service.repository;

import vn.com.be_crm.core.util.ListQueryUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.core.dto.delete.DeletedItemResult;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.page.PageResult;
import vn.com.be_crm.domain.service.entity.Ticket;
import vn.com.be_crm.domain.service.entity.TicketReturnItem;
import vn.com.be_crm.domain.service.repository.ITicketRepository;
import vn.com.be_crm.infrastructure.service.entity.TicketHibernate;
import vn.com.be_crm.infrastructure.service.entity.TicketReturnItemHibernate;
import vn.com.be_crm.infrastructure.service.mapper.TicketHibernateMapper;
import vn.com.be_crm.infrastructure.service.mapper.TicketReturnItemHibernateMapper;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import vn.com.be_crm.core.tx.impl.TxSupport;

/**
 * Hibernate implementation của ITicketRepository.
 */
@Repository
public class TicketRepositoryImpl implements ITicketRepository {
    private final SessionFactory sf;
    private final TicketHibernateMapper mapper;
    private final TicketReturnItemHibernateMapper itemMapper;

    /** @param sf Hibernate SessionFactory @param mapper mapper phiếu @param itemMapper mapper dòng hàng */
    public TicketRepositoryImpl(SessionFactory sf, TicketHibernateMapper mapper,
                                TicketReturnItemHibernateMapper itemMapper) {
        this.sf = sf; this.mapper = mapper; this.itemMapper = itemMapper;
    }

    /** Lưu mới hoặc cập nhật phiếu. @param t @return entity sau khi lưu */
    @Override public Ticket save(Ticket t) {
        return TxSupport.write(sf, s -> {
            TicketHibernate m = s.merge(mapper.toHibernate(t));
            return mapper.toDomain(m);
        });
    }

    /** Tìm phiếu theo mã (chưa xóa mềm). @param code mã @return Optional */
    @Override public Optional<Ticket> findByCode(String code) {
        return TxSupport.read(sf, s -> {
            return s.createQuery("FROM TicketHibernate WHERE code = :code AND deletedAt IS NULL", TicketHibernate.class)
                    .setParameter("code", code).setMaxResults(1).list()
                    .stream().map(mapper::toDomain).findFirst();
        });
    }

    /**
     * Lưu phiếu kèm danh sách dòng hàng trả/đổi trong MỘT transaction.
     * @param t     domain entity phiếu
     * @param items danh sách dòng hàng
     * @return phiếu sau khi lưu
     */
    @Override public Ticket saveWithReturnItems(Ticket t, List<TicketReturnItem> items) {
        return TxSupport.write(sf, s -> {
            TicketHibernate m = s.merge(mapper.toHibernate(t));
            for (TicketReturnItem item : items) {
                TicketReturnItemHibernate ih = itemMapper.toHibernate(item);
                ih.setTicketId(m.getId());
                s.merge(ih);
            }
            return mapper.toDomain(m);
        });
    }

    /** Tìm phiếu theo ID — chỉ trả về nếu chưa xóa mềm. @param id @return Optional */
    @Override public Optional<Ticket> findById(Long id) {
        return TxSupport.read(sf, s -> {
            TicketHibernate h = s.find(TicketHibernate.class, id);
            if (h == null || h.getDeletedAt() != null) return Optional.empty();
            return Optional.of(mapper.toDomain(h));
        });
    }

    /** Xóa mềm phiếu, ghi nhận người xóa. @param id ID @param deletedBy userId người xóa */
    @Override public void deleteById(Long id, Long deletedBy) {
        TxSupport.writeVoid(sf, s -> {
            TicketHibernate h = s.find(TicketHibernate.class, id);
            if (h != null) { h.setDeletedAt(LocalDateTime.now()); h.setDeletedBy(deletedBy); s.merge(h); }
            });
    }

    /** Lấy danh sách phiếu chưa xóa có phân trang. @param r @return PageResult */
    @Override public PageResult<Ticket> findAll(PageRequest r) {
        return TxSupport.read(sf, s -> {
            String yearFilter = r.getDataAccessFromYear() != null ? " AND YEAR(createdAt) >= :fromYear" : "";
            String ownerFilter = r.getOwnerId() != null ? " AND assignedUserId = :ownerId" : "";
            String searchFilter = ListQueryUtils.likeClause(r.getQ(), "code", "subject");
            var statusVal = ListQueryUtils.parseEnum(vn.com.be_crm.domain.service.enums.TicketType.class, r.getStatus());
            String statusFilter = statusVal != null ? " AND type = :status" : "";
            String where = " WHERE deletedAt IS NULL" + yearFilter + ownerFilter + searchFilter + statusFilter;
            String orderBy = " ORDER BY " + ListQueryUtils.safeSortBy(r.getSortBy(), "createdAt") + " " + ListQueryUtils.safeSortDir(r.getSortDir());
            var q = s.createQuery("FROM TicketHibernate" + where + orderBy, TicketHibernate.class)
                    .setFirstResult(r.getOffset()).setMaxResults(r.getSize());
            var cq = s.createQuery("SELECT COUNT(*) FROM TicketHibernate" + where, Long.class);
            for (var query : List.of(q, cq)) {
                if (r.getDataAccessFromYear() != null) query.setParameter("fromYear", r.getDataAccessFromYear());
                if (r.getOwnerId() != null) query.setParameter("ownerId", r.getOwnerId());
                if (!searchFilter.isEmpty()) query.setParameter("q", ListQueryUtils.likeParam(r.getQ()));
                if (statusVal != null) query.setParameter("status", statusVal);
            }
            List<Ticket> items = q.list().stream().map(mapper::toDomain).collect(Collectors.toList());
            long total = cq.uniqueResult();
            return PageResult.<Ticket>builder().items(items).total(total).page(r.getPage()).size(r.getSize()).build();
        });
    }

    /** Lấy danh sách phiếu trong thùng rác (30 ngày). @param userId ID người dùng @param isAdmin admin thấy tất cả @param req phân trang */
    @Override public PageResult<DeletedItemResult> findDeleted(Long userId, boolean isAdmin, PageRequest req) {
        return TxSupport.read(sf, s -> {
            LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
            String userFilter = isAdmin ? "" : " AND t.deleted_by = :userId";
            String sql = "SELECT t.id, t.code, t.deleted_at, u.full_name FROM support_tickets t" +
                    " LEFT JOIN users u ON u.id = t.deleted_by" +
                    " WHERE t.deleted_at IS NOT NULL AND t.deleted_at >= :cutoff AND t.is_purged = 0" +
                    userFilter + " ORDER BY t.deleted_at DESC";
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
            String countSql = "SELECT COUNT(*) FROM support_tickets t WHERE t.deleted_at IS NOT NULL AND t.deleted_at >= :cutoff AND t.is_purged = 0" + userFilter;
            var cq = s.createNativeQuery(countSql, Object.class).setParameter("cutoff", cutoff);
            if (!isAdmin) cq.setParameter("userId", userId);
            long total = ((Number) cq.uniqueResult()).longValue();
            return PageResult.<DeletedItemResult>builder().items(items).total(total).page(req.getPage()).size(req.getSize()).build();
        });
    }

    /** Khôi phục phiếu từ thùng rác. @param id ID */
    @Override public void restoreById(Long id) {
        TxSupport.writeVoid(sf, s -> {
            TicketHibernate h = s.find(TicketHibernate.class, id);
            if (h != null) { h.setDeletedAt(null); h.setDeletedBy(null); h.setPurged(false); s.merge(h); }
            });
    }

    /** Ẩn phiếu khỏi thùng rác (is_purged = true). @param id ID */
    @Override public void purgeById(Long id) {
        TxSupport.writeVoid(sf, s -> {
            TicketHibernate h = s.find(TicketHibernate.class, id);
            if (h != null) { h.setPurged(true); s.merge(h); }
            });
    }

    /** Bàn giao hàng loạt phiếu sang nhân viên xử lý mới. @param ids IDs @param toUserId người nhận @param currentUserId người thực hiện @param isAdminOrManager quyền admin/manager */
    @Override public void handoverBulk(List<Long> ids, Long toUserId, Long currentUserId, boolean isAdminOrManager) {
        if (ids == null || ids.isEmpty()) return;
        TxSupport.writeVoid(sf, s -> {
            String ownerFilter = isAdminOrManager ? "" : " AND assigned_user_id = :currentUserId";
            String sql = "UPDATE support_tickets SET assigned_user_id = :toUserId WHERE id IN (:ids) AND deleted_at IS NULL" + ownerFilter;
            var q = s.createNativeQuery(sql).setParameter("toUserId", toUserId).setParameter("ids", ids);
            if (!isAdminOrManager) q.setParameter("currentUserId", currentUserId);
            q.executeUpdate();
            });
    }
}
