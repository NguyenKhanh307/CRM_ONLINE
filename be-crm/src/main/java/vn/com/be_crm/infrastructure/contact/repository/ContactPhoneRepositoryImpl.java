package vn.com.be_crm.infrastructure.contact.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.contact.entity.ContactPhone;
import vn.com.be_crm.domain.contact.repository.IContactPhoneRepository;
import vn.com.be_crm.infrastructure.contact.entity.ContactPhoneHibernate;
import vn.com.be_crm.infrastructure.contact.mapper.ContactPhoneHibernateMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Hibernate implementation của IContactPhoneRepository.
 */
@Repository
public class ContactPhoneRepositoryImpl implements IContactPhoneRepository {
    private final SessionFactory sf;
    private final ContactPhoneHibernateMapper mapper;

    /**
     * @param sf     Hibernate SessionFactory
     * @param mapper mapper domain ↔ hibernate
     */
    public ContactPhoneRepositoryImpl(SessionFactory sf, ContactPhoneHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /**
     * Lưu mới hoặc cập nhật ContactPhone.
     * @param phone domain entity @return entity sau khi lưu
     */
    @Override
    public ContactPhone save(ContactPhone phone) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            ContactPhoneHibernate m = s.merge(mapper.toHibernate(phone));
            tx.commit(); return mapper.toDomain(m);
        }
    }

    /**
     * Tìm ContactPhone theo ID.
     * @param id ID @return Optional
     */
    @Override
    public Optional<ContactPhone> findById(Long id) {
        try (Session s = sf.openSession()) {
            ContactPhoneHibernate h = s.find(ContactPhoneHibernate.class, id);
            return Optional.ofNullable(h).map(mapper::toDomain);
        }
    }

    /**
     * Xóa ContactPhone theo ID.
     * @param id ID cần xóa
     */
    @Override
    public void deleteById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            ContactPhoneHibernate h = s.find(ContactPhoneHibernate.class, id);
            if (h != null) s.remove(h);
            tx.commit();
        }
    }

    /**
     * Lấy danh sách ContactPhone theo contactId.
     * @param contactId ID liên hệ @return danh sách
     */
    @Override
    public List<ContactPhone> findAllByContactId(Long contactId) {
        try (Session s = sf.openSession()) {
            return s.createQuery("FROM ContactPhoneHibernate WHERE contactId = :cid", ContactPhoneHibernate.class)
                    .setParameter("cid", contactId).list().stream().map(mapper::toDomain).collect(Collectors.toList());
        }
    }
}
