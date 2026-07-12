package vn.com.be_crm.infrastructure.customer.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.customer.entity.CustomerShare;
import vn.com.be_crm.domain.customer.repository.ICustomerShareRepository;
import vn.com.be_crm.infrastructure.customer.entity.CustomerShareHibernate;
import vn.com.be_crm.infrastructure.customer.mapper.CustomerShareHibernateMapper;

import java.util.List;
import java.util.stream.Collectors;
import vn.com.be_crm.infrastructure.shared.tx.TxSupport;

/**
 * Hibernate implementation của ICustomerShareRepository.
 */
@Repository
public class CustomerShareRepositoryImpl implements ICustomerShareRepository {
    private final SessionFactory sf;
    private final CustomerShareHibernateMapper mapper;

    /**
     * @param sf     Hibernate SessionFactory
     * @param mapper mapper domain ↔ hibernate
     */
    public CustomerShareRepositoryImpl(SessionFactory sf, CustomerShareHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /**
     * Lưu bản ghi chia sẻ.
     * @param share domain entity @return entity sau khi lưu
     */
    @Override
    public CustomerShare save(CustomerShare share) {
        return TxSupport.write(sf, s -> {
            CustomerShareHibernate m = s.merge(mapper.toHibernate(share));
            return mapper.toDomain(m);
        });
    }

    /**
     * Xóa bản ghi chia sẻ theo customerId và userId.
     * @param customerId ID khách hàng @param userId ID người dùng
     */
    @Override
    public void deleteByCustomerIdAndUserId(Long customerId, Long userId) {
        TxSupport.writeVoid(sf, s -> {
            s.createMutationQuery("DELETE FROM CustomerShareHibernate WHERE customerId = :cid AND userId = :uid")
                    .setParameter("cid", customerId).setParameter("uid", userId).executeUpdate();
            });
    }

    /**
     * Lấy danh sách chia sẻ theo customerId.
     * @param customerId ID khách hàng @return danh sách CustomerShare
     */
    @Override
    public List<CustomerShare> findByCustomerId(Long customerId) {
        return TxSupport.read(sf, s -> {
            return s.createQuery("FROM CustomerShareHibernate WHERE customerId = :cid", CustomerShareHibernate.class)
                    .setParameter("cid", customerId).list().stream().map(mapper::toDomain).collect(Collectors.toList());
        });
    }
}
