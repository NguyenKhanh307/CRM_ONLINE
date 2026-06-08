package vn.com.be_crm.infrastructure.lead.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.lead.entity.Lead;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;
import vn.com.be_crm.infrastructure.lead.entity.LeadHibernate;
import vn.com.be_crm.infrastructure.lead.mapper.LeadHibernateMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            LeadHibernate m = s.merge(mapper.toHibernate(l));
            tx.commit(); return mapper.toDomain(m);
        }
    }

    /** Tìm Lead theo ID — chỉ trả về nếu chưa xóa mềm. @param id ID @return Optional */
    @Override public Optional<Lead> findById(Long id) {
        try (Session s = sf.openSession()) {
            LeadHibernate h = s.find(LeadHibernate.class, id);
            if (h == null || h.getDeletedAt() != null) return Optional.empty();
            return Optional.of(mapper.toDomain(h));
        }
    }

    /** Xóa mềm Lead. @param id ID */
    @Override public void deleteById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            LeadHibernate h = s.find(LeadHibernate.class, id);
            if (h != null) { h.setDeletedAt(LocalDateTime.now()); s.merge(h); }
            tx.commit();
        }
    }

    /** Lấy danh sách Lead chưa xóa có phân trang. @param r phân trang @return PageResult */
    @Override public PageResult<Lead> findAll(PageRequest r) {
        try (Session s = sf.openSession()) {
            List<Lead> items = s.createQuery(
                    "FROM LeadHibernate WHERE deletedAt IS NULL ORDER BY " + r.getSortBy() + " " + r.getSortDir(),
                    LeadHibernate.class)
                    .setFirstResult(r.getOffset()).setMaxResults(r.getSize())
                    .list().stream().map(mapper::toDomain).collect(Collectors.toList());
            long total = s.createQuery("SELECT COUNT(l) FROM LeadHibernate l WHERE l.deletedAt IS NULL", Long.class).uniqueResult();
            return PageResult.<Lead>builder().items(items).total(total).page(r.getPage()).size(r.getSize()).build();
        }
    }
}
