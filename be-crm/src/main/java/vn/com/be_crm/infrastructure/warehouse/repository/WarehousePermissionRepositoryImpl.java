package vn.com.be_crm.infrastructure.warehouse.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.domain.warehouse.entity.WarehousePermission;
import vn.com.be_crm.domain.warehouse.repository.IWarehousePermissionRepository;
import vn.com.be_crm.infrastructure.warehouse.entity.WarehousePermissionHibernate;
import vn.com.be_crm.infrastructure.warehouse.mapper.WarehousePermissionHibernateMapper;

import java.util.List;
import java.util.stream.Collectors;

/** Hibernate implementation của IWarehousePermissionRepository. */
@Repository
public class WarehousePermissionRepositoryImpl implements IWarehousePermissionRepository {
    private final SessionFactory sf;
    private final WarehousePermissionHibernateMapper mapper;
    /**
     * @param sf SessionFactory @param mapper mapper
     */
    public WarehousePermissionRepositoryImpl(SessionFactory sf, WarehousePermissionHibernateMapper mapper) { this.sf = sf; this.mapper = mapper; }
    /** @param p entity @return saved */
    @Override public WarehousePermission save(WarehousePermission p) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            WarehousePermissionHibernate m = s.merge(mapper.toHibernate(p)); tx.commit(); return mapper.toDomain(m);
        }
    }
    /** @param warehouseId ID kho @param userId ID user */
    @Override public void deleteByWarehouseIdAndUserId(Long warehouseId, Long userId) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            s.createMutationQuery("DELETE FROM WarehousePermissionHibernate WHERE warehouseId=:wid AND userId=:uid")
                    .setParameter("wid", warehouseId).setParameter("uid", userId).executeUpdate();
            tx.commit();
        }
    }
    /** @param warehouseId ID kho @return list */
    @Override public List<WarehousePermission> findByWarehouseId(Long warehouseId) {
        try (Session s = sf.openSession()) {
            return s.createQuery("FROM WarehousePermissionHibernate WHERE warehouseId=:wid", WarehousePermissionHibernate.class)
                    .setParameter("wid", warehouseId).list().stream().map(mapper::toDomain).collect(Collectors.toList());
        }
    }
}
