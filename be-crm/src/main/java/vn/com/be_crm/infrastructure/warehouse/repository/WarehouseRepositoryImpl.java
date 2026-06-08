package vn.com.be_crm.infrastructure.warehouse.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.warehouse.entity.Warehouse;
import vn.com.be_crm.domain.warehouse.repository.IWarehouseRepository;
import vn.com.be_crm.infrastructure.warehouse.entity.WarehouseHibernate;
import vn.com.be_crm.infrastructure.warehouse.mapper.WarehouseHibernateMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/** Hibernate implementation của IWarehouseRepository. */
@Repository
public class WarehouseRepositoryImpl implements IWarehouseRepository {
    private final SessionFactory sf;
    private final WarehouseHibernateMapper mapper;
    /**
     * @param sf SessionFactory @param mapper mapper
     */
    public WarehouseRepositoryImpl(SessionFactory sf, WarehouseHibernateMapper mapper) { this.sf = sf; this.mapper = mapper; }
    /** @param w entity @return saved */
    @Override public Warehouse save(Warehouse w) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            WarehouseHibernate m = s.merge(mapper.toHibernate(w)); tx.commit(); return mapper.toDomain(m);
        }
    }
    /** @param id ID @return Optional */
    @Override public Optional<Warehouse> findById(Long id) {
        try (Session s = sf.openSession()) {
            return Optional.ofNullable(s.find(WarehouseHibernate.class, id)).map(mapper::toDomain);
        }
    }
    /** @param id ID to delete */
    @Override public void deleteById(Long id) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            WarehouseHibernate h = s.find(WarehouseHibernate.class, id);
            if (h != null) s.remove(h); tx.commit();
        }
    }
    /** @param r page request @return PageResult */
    @Override public PageResult<Warehouse> findAll(PageRequest r) {
        try (Session s = sf.openSession()) {
            List<Warehouse> items = s.createQuery("FROM WarehouseHibernate ORDER BY " + r.getSortBy() + " " + r.getSortDir(), WarehouseHibernate.class)
                    .setFirstResult(r.getOffset()).setMaxResults(r.getSize())
                    .list().stream().map(mapper::toDomain).collect(Collectors.toList());
            long total = s.createQuery("SELECT COUNT(w) FROM WarehouseHibernate w", Long.class).uniqueResult();
            return PageResult.<Warehouse>builder().items(items).total(total).page(r.getPage()).size(r.getSize()).build();
        }
    }
}
