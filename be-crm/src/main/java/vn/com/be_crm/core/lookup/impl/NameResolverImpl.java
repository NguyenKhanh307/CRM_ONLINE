package vn.com.be_crm.core.lookup.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.core.lookup.port.INameResolver;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Hibernate implementation của {@link INameResolver}.
 *
 * <p>
 * Mỗi method chạy một native query {@code SELECT id, <col> FROM 
 * 
<table>
 *  WHERE id IN (:ids)}
 * — <b>không</b> lọc {@code deleted_at}/{@code is_purged} nên tên vẫn resolve
 * cho bản ghi đã xóa.
 */
@Repository
public class NameResolverImpl implements INameResolver {

    private final SessionFactory sf;

    /** @param sf Hibernate SessionFactory */
    public NameResolverImpl(SessionFactory sf) {
        this.sf = sf;
    }

    // hàm resolve tên cho bảng users
    @Override
    public Map<Long, String> users(Collection<Long> ids) {
        return resolve("users", "full_name", ids);
    }

    // hàm resolve tên cho bảng customers
    @Override
    public Map<Long, String> customers(Collection<Long> ids) {
        return resolve("customers", "name", ids);
    }

    // hàm resolve tên cho bảng contacts
    @Override
    public Map<Long, String> contacts(Collection<Long> ids) {
        return resolve("contacts", "full_name", ids);
    }

    // hàm resolve tên cho bảng campaigns
    @Override
    public Map<Long, String> campaigns(Collection<Long> ids) {
        return resolve("campaigns", "name", ids);
    }

    // hàm resolve tên cho bảng opportunities
    @Override
    public Map<Long, String> opportunities(Collection<Long> ids) {
        return resolve("opportunities", "name", ids);
    }

    // hàm resolve tên cho bảng quotations
    @Override
    public Map<Long, String> quotationCodes(Collection<Long> ids) {
        return resolve("quotations", "code", ids);
    }

    // hàm resolve tên cho bảng orders
    @Override
    public Map<Long, String> orderCodes(Collection<Long> ids) {
        return resolve("orders", "code", ids);
    }

    // hàm resolve tên cho bảng invoices, opportunities, product_categories,
    // products
    @Override
    public Map<Long, String> invoiceCodes(Collection<Long> ids) {
        return resolve("invoices", "code", ids);
    }

    // hàm resolve tên cho bảng opportunity_stages
    @Override
    public Map<Long, String> stages(Collection<Long> ids) {
        return resolve("opportunity_stages", "name", ids);
    }

    // hàm resolve tên cho bảng product_categories
    @Override
    public Map<Long, String> productCategories(Collection<Long> ids) {
        return resolve("product_categories", "name", ids);
    }

    // hàm resolve tên cho bảng products
    @Override
    public Map<Long, String> products(Collection<Long> ids) {
        return resolve("products", "name", ids);
    }

    /**
     * Chạy native query tra id→tên cho một bảng.
     * Tham số bảng/cột là hằng nội bộ (không nhận từ input người dùng) nên không có
     * rủi ro SQL injection.
     * 
     * @param table   tên bảng
     * @param nameCol cột chứa tên/mã hiển thị
     * @param ids     tập ID (null/rỗng → trả map rỗng)
     * @return map id → tên (bỏ qua null)
     */
    private Map<Long, String> resolve(String table, String nameCol, Collection<Long> ids) {
        // Trả HashMap rỗng (mutable, get(null)-safe) thay Map.of() — Map bất biến ném
        // NPE khi get(null).
        if (ids == null || ids.isEmpty())
            return new HashMap<>();
        // Gom ID duy nhất, bỏ null
        Set<Long> distinct = ids.stream().filter(java.util.Objects::nonNull).collect(Collectors.toSet());
        if (distinct.isEmpty())
            return new HashMap<>();
        try (Session s = sf.openSession()) {
            String sql = "SELECT id, " + nameCol + " FROM " + table + " WHERE id IN (:ids)";
            var rows = s.createNativeQuery(sql, Object[].class).setParameter("ids", distinct).list();
            Map<Long, String> map = new HashMap<>();
            for (Object[] row : rows) {
                if (row[0] == null)
                    continue;
                Long id = ((Number) row[0]).longValue();
                map.put(id, row[1] != null ? row[1].toString() : null);
            }
            return map;
        }
    }
}
