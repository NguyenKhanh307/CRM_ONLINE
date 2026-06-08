package vn.com.be_crm.infrastructure.opportunity.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.opportunity.entity.Opportunity;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository;
import vn.com.be_crm.infrastructure.opportunity.entity.OpportunityHibernate;
import vn.com.be_crm.infrastructure.opportunity.mapper.OpportunityHibernateMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Hibernate implementation của IOpportunityRepository.
 * Soft delete: deleteById set deleted_at = now().
 */
@Repository
public class OpportunityRepositoryImpl implements IOpportunityRepository {
    private final SessionFactory sf;
    private final OpportunityHibernateMapper mapper;

    /** @param sf Hibernate SessionFactory @param mapper mapper */
    public OpportunityRepositoryImpl(SessionFactory sf, OpportunityHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /** Lưu mới hoặc cập nhật Opportunity. @param o domain entity @return entity sau khi lưu */
    @Override public Opportunity save(Opportunity o) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            OpportunityHibernate m = s.merge(mapper.toHibernate(o));
            tx.commit(); return mapper.toDomain(m);
        }
    }

    /** Tìm Opportunity theo ID — chỉ trả về nếu chưa xóa mềm. @param id ID @return Optional */
    @Override public Optional<Opportunity> findById(Long id) {
        try (Session s = sf.openSession()) {
            OpportunityHibernate h = s.find(OpportunityHibernate.class, id);
            if (h == null || h.getDeletedAt() != null) return Optional.empty();
            return Optional.of(mapper.toDomain(h));
        }
    }

    /** Xóa mềm Opportunity. @param id ID */
    @Override public void deleteById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            OpportunityHibernate h = s.find(OpportunityHibernate.class, id);
            if (h != null) { h.setDeletedAt(LocalDateTime.now()); s.merge(h); }
            tx.commit();
        }
    }

    /** Lấy danh sách Opportunity chưa xóa có phân trang. @param r phân trang @return PageResult */
    @Override public PageResult<Opportunity> findAll(PageRequest r) {
        try (Session s = sf.openSession()) {
            List<Opportunity> items = s.createQuery(
                    "FROM OpportunityHibernate WHERE deletedAt IS NULL ORDER BY " + r.getSortBy() + " " + r.getSortDir(),
                    OpportunityHibernate.class)
                    .setFirstResult(r.getOffset()).setMaxResults(r.getSize())
                    .list().stream().map(mapper::toDomain).collect(Collectors.toList());
            long total = s.createQuery("SELECT COUNT(o) FROM OpportunityHibernate o WHERE o.deletedAt IS NULL", Long.class).uniqueResult();
            return PageResult.<Opportunity>builder().items(items).total(total).page(r.getPage()).size(r.getSize()).build();
        }
    }
}
