package vn.com.be_crm.infrastructure.service.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.service.entity.SlaPolicy;
import vn.com.be_crm.domain.service.enums.TicketPriority;
import vn.com.be_crm.domain.service.repository.ISlaPolicyRepository;
import vn.com.be_crm.infrastructure.service.entity.SlaPolicyHibernate;
import vn.com.be_crm.infrastructure.service.mapper.SlaPolicyHibernateMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import vn.com.be_crm.core.tx.impl.TxSupport;

/**
 * Hibernate implementation của ISlaPolicyRepository.
 */
@Repository
public class SlaPolicyRepositoryImpl implements ISlaPolicyRepository {
    private final SessionFactory sf;
    private final SlaPolicyHibernateMapper mapper;

    /** @param sf Hibernate SessionFactory @param mapper mapper */
    public SlaPolicyRepositoryImpl(SessionFactory sf, SlaPolicyHibernateMapper mapper) {
        this.sf = sf; this.mapper = mapper;
    }

    /** Lấy toàn bộ chính sách SLA (theo độ ưu tiên tăng dần theo id). @return danh sách */
    @Override public List<SlaPolicy> findAll() {
        return TxSupport.read(sf, s -> {
            return s.createQuery("FROM SlaPolicyHibernate ORDER BY id", SlaPolicyHibernate.class)
                    .list().stream().map(mapper::toDomain).collect(Collectors.toList());
        });
    }

    /** Tìm chính sách SLA còn hiệu lực theo độ ưu tiên. @param priority độ ưu tiên @return Optional */
    @Override public Optional<SlaPolicy> findByPriority(TicketPriority priority) {
        return TxSupport.read(sf, s -> {
            return s.createQuery("FROM SlaPolicyHibernate WHERE priority = :p AND isActive = true", SlaPolicyHibernate.class)
                    .setParameter("p", priority).setMaxResults(1).list()
                    .stream().map(mapper::toDomain).findFirst();
        });
    }
}
