package vn.com.be_crm.infrastructure.lead.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.lead.entity.LeadAttachment;
import vn.com.be_crm.domain.lead.repository.ILeadAttachmentRepository;
import vn.com.be_crm.infrastructure.lead.entity.LeadAttachmentHibernate;
import vn.com.be_crm.infrastructure.lead.mapper.LeadAttachmentHibernateMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Hibernate implementation của ILeadAttachmentRepository.
 */
@Repository
public class LeadAttachmentRepositoryImpl implements ILeadAttachmentRepository {
    private final SessionFactory sf;
    private final LeadAttachmentHibernateMapper mapper;

    /** @param sf Hibernate SessionFactory @param mapper mapper */
    public LeadAttachmentRepositoryImpl(SessionFactory sf, LeadAttachmentHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /** Lưu mới hoặc cập nhật LeadAttachment. @param a domain entity @return entity sau khi lưu */
    @Override public LeadAttachment save(LeadAttachment a) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            LeadAttachmentHibernate m = s.merge(mapper.toHibernate(a));
            tx.commit(); return mapper.toDomain(m);
        }
    }

    /** Tìm LeadAttachment theo ID. @param id ID @return Optional */
    @Override public Optional<LeadAttachment> findById(Long id) {
        try (Session s = sf.openSession()) {
            LeadAttachmentHibernate h = s.find(LeadAttachmentHibernate.class, id);
            return Optional.ofNullable(h).map(mapper::toDomain);
        }
    }

    /** Xóa LeadAttachment theo ID. @param id ID */
    @Override public void deleteById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            LeadAttachmentHibernate h = s.find(LeadAttachmentHibernate.class, id);
            if (h != null) s.remove(h);
            tx.commit();
        }
    }

    /** Lấy danh sách LeadAttachment theo leadId. @param leadId ID @return danh sách */
    @Override public List<LeadAttachment> findAllByLeadId(Long leadId) {
        try (Session s = sf.openSession()) {
            return s.createQuery("FROM LeadAttachmentHibernate WHERE leadId = :lid", LeadAttachmentHibernate.class)
                    .setParameter("lid", leadId).list().stream().map(mapper::toDomain).collect(Collectors.toList());
        }
    }
}
