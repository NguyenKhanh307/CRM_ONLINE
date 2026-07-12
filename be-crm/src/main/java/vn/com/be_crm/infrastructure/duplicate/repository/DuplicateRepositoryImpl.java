package vn.com.be_crm.infrastructure.duplicate.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.application.duplicate.dto.DuplicateMatch;
import vn.com.be_crm.domain.duplicate.repository.IDuplicateRepository;
import vn.com.be_crm.infrastructure.shared.tx.TxSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hibernate implementation của IDuplicateRepository — dò trùng bằng native SQL trên 3 bảng
 * (leads, customers, contacts + contact_phones) trong một session.
 */
@Repository
public class DuplicateRepositoryImpl implements IDuplicateRepository {

    /** Số bản ghi trùng tối đa trả về cho mỗi phân hệ. */
    private static final int LIMIT = 5;

    private final SessionFactory sf;

    /** @param sf Hibernate SessionFactory */
    public DuplicateRepositoryImpl(SessionFactory sf) {
        this.sf = sf;
    }

    /** {@inheritDoc} */
    @Override
    public List<DuplicateMatch> find(String email, String phone, String taxCode, String excludeModule, Long excludeId) {
        String e = trim(email);
        String p = trim(phone);
        String t = trim(taxCode);
        if (e == null && p == null && t == null) return List.of();

        return TxSupport.read(sf, s -> {
            List<DuplicateMatch> out = new ArrayList<>();
            out.addAll(scanLeadsOrCustomers(s, "leads", "lead", e, p, t, excludeModule, excludeId));
            out.addAll(scanLeadsOrCustomers(s, "customers", "customer", e, p, t, excludeModule, excludeId));
            out.addAll(scanContacts(s, e, p, excludeModule, excludeId));
            return out;
        });
    }

    /**
     * Dò trùng trên bảng leads hoặc customers (cùng cấu trúc cột email/phone/tax_code/code/name).
     *
     * @param table  tên bảng SQL
     * @param module khóa phân hệ trả về trong kết quả
     */
    @SuppressWarnings("unchecked")
    private List<DuplicateMatch> scanLeadsOrCustomers(Session s, String table, String module,
                                                      String email, String phone, String taxCode,
                                                      String excludeModule, Long excludeId) {
        List<String> conds = new ArrayList<>();
        if (email != null) conds.add("email = :email");
        if (phone != null) conds.add("phone = :phone");
        if (taxCode != null) conds.add("tax_code = :taxCode");

        boolean exclude = module.equals(excludeModule) && excludeId != null;
        String sql = "SELECT id, code, name, "
                + "CASE WHEN " + (email != null ? "email = :email" : "FALSE") + " THEN 'email' "
                + "WHEN " + (phone != null ? "phone = :phone" : "FALSE") + " THEN 'phone' ELSE 'taxCode' END, "
                + "CASE WHEN " + (email != null ? "email = :email" : "FALSE") + " THEN email "
                + "WHEN " + (phone != null ? "phone = :phone" : "FALSE") + " THEN phone ELSE tax_code END "
                + "FROM " + table + " WHERE deleted_at IS NULL AND (" + String.join(" OR ", conds) + ")"
                + (exclude ? " AND id <> :excludeId" : "")
                + " ORDER BY id DESC LIMIT " + LIMIT;

        var q = s.createNativeQuery(sql, Object[].class);
        if (email != null) q.setParameter("email", email);
        if (phone != null) q.setParameter("phone", phone);
        if (taxCode != null) q.setParameter("taxCode", taxCode);
        if (exclude) q.setParameter("excludeId", excludeId);

        List<DuplicateMatch> out = new ArrayList<>();
        for (Object[] r : (List<Object[]>) q.list()) {
            out.add(new DuplicateMatch(module, num(r[0]), str(r[1]), str(r[2]), str(r[3]), str(r[4])));
        }
        return out;
    }

    /**
     * Dò trùng trên liên hệ: email (3 cột email) và số điện thoại (bảng contact_phones).
     * Liên hệ không có mã và không có mã số thuế.
     */
    @SuppressWarnings("unchecked")
    private List<DuplicateMatch> scanContacts(Session s, String email, String phone,
                                              String excludeModule, Long excludeId) {
        if (email == null && phone == null) return List.of();

        List<String> conds = new ArrayList<>();
        if (email != null) conds.add("(c.email = :email OR c.work_email = :email OR c.personal_email = :email)");
        if (phone != null) conds.add("EXISTS (SELECT 1 FROM contact_phones cp WHERE cp.contact_id = c.id AND cp.phone = :phone)");

        boolean exclude = "contact".equals(excludeModule) && excludeId != null;
        String sql = "SELECT c.id, c.full_name, "
                + "CASE WHEN " + (email != null ? "(c.email = :email OR c.work_email = :email OR c.personal_email = :email)" : "FALSE")
                + " THEN 'email' ELSE 'phone' END "
                + "FROM contacts c WHERE c.deleted_at IS NULL AND (" + String.join(" OR ", conds) + ")"
                + (exclude ? " AND c.id <> :excludeId" : "")
                + " ORDER BY c.id DESC LIMIT " + LIMIT;

        var q = s.createNativeQuery(sql, Object[].class);
        if (email != null) q.setParameter("email", email);
        if (phone != null) q.setParameter("phone", phone);
        if (exclude) q.setParameter("excludeId", excludeId);

        Map<String, String> values = new HashMap<>();
        if (email != null) values.put("email", email);
        if (phone != null) values.put("phone", phone);

        List<DuplicateMatch> out = new ArrayList<>();
        for (Object[] r : (List<Object[]>) q.list()) {
            String field = str(r[2]);
            out.add(new DuplicateMatch("contact", num(r[0]), null, str(r[1]), field, values.get(field)));
        }
        return out;
    }

    /** Chuẩn hóa tham số: rỗng/khoảng trắng → null (bỏ dò trường đó). */
    private String trim(String v) {
        return v == null || v.isBlank() ? null : v.trim();
    }

    /** Đổi cột số sang Long (null-safe). */
    private Long num(Object v) {
        return v == null ? null : ((Number) v).longValue();
    }

    /** Đổi cột chuỗi sang String (null-safe). */
    private String str(Object v) {
        return v == null ? null : String.valueOf(v);
    }
}
