package vn.com.be_crm.infrastructure.warehouse.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.warehouse.entity.InventoryStock;
import vn.com.be_crm.domain.warehouse.repository.IInventoryStockRepository;
import vn.com.be_crm.infrastructure.warehouse.entity.InventoryStockHibernate;
import vn.com.be_crm.infrastructure.warehouse.mapper.InventoryStockHibernateMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/** Hibernate implementation của IInventoryStockRepository. */
@Repository
public class InventoryStockRepositoryImpl implements IInventoryStockRepository {
    private final SessionFactory sf;
    private final InventoryStockHibernateMapper mapper;
    /**
     * @param sf SessionFactory @param mapper mapper
     */
    public InventoryStockRepositoryImpl(SessionFactory sf, InventoryStockHibernateMapper mapper) { this.sf = sf; this.mapper = mapper; }
    /** @param stock entity @return saved */
    @Override public InventoryStock save(InventoryStock stock) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            InventoryStockHibernate m = s.merge(mapper.toHibernate(stock)); tx.commit(); return mapper.toDomain(m);
        }
    }
    /** @param id ID @return Optional */
    @Override public Optional<InventoryStock> findById(Long id) {
        try (Session s = sf.openSession()) {
            return Optional.ofNullable(s.find(InventoryStockHibernate.class, id)).map(mapper::toDomain);
        }
    }
    /** @param r page request @return PageResult */
    @Override public PageResult<InventoryStock> findAll(PageRequest r) {
        try (Session s = sf.openSession()) {
            List<InventoryStock> items = s.createQuery("FROM InventoryStockHibernate ORDER BY " + r.getSortBy() + " " + r.getSortDir(), InventoryStockHibernate.class)
                    .setFirstResult(r.getOffset()).setMaxResults(r.getSize())
                    .list().stream().map(mapper::toDomain).collect(Collectors.toList());
            long total = s.createQuery("SELECT COUNT(i) FROM InventoryStockHibernate i", Long.class).uniqueResult();
            return PageResult.<InventoryStock>builder().items(items).total(total).page(r.getPage()).size(r.getSize()).build();
        }
    }
}
