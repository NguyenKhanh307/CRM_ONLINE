package vn.com.be_crm.core.util;

/**
 * Tiện ích dựng mệnh đề HQL động cho các list query (owner filter, search LIKE, sort an toàn).
 * Dùng chung cho mọi *RepositoryImpl.findAll.
 */
public final class ListQueryUtils {

    private ListQueryUtils() {}

    /**
     * Chống HQL injection cho sortBy: chỉ chấp nhận tên field Java hợp lệ (chữ/số/underscore),
     * ngoài ra trả về fallback.
     *
     * @param sortBy   tên field client gửi lên
     * @param fallback field mặc định khi sortBy không hợp lệ
     * @return tên field an toàn để nối vào ORDER BY
     */
    public static String safeSortBy(String sortBy, String fallback) {
        return sortBy != null && sortBy.matches("[A-Za-z][A-Za-z0-9_]{0,49}") ? sortBy : fallback;
    }

    /**
     * Chuẩn hóa chiều sắp xếp — chỉ nhận asc/desc.
     *
     * @param sortDir chiều sắp xếp client gửi lên
     * @return "asc" hoặc "desc"
     */
    public static String safeSortDir(String sortDir) {
        return "desc".equalsIgnoreCase(sortDir) ? "desc" : "asc";
    }

    /**
     * Dựng mệnh đề search LIKE trên nhiều field: " AND (f1 LIKE :q OR f2 LIKE :q ...)".
     * Trả chuỗi rỗng khi không có từ khóa. Caller phải bind tham số :q = likeParam(q).
     *
     * @param q      từ khóa tìm kiếm (có thể null)
     * @param fields danh sách tên field HQL để so LIKE
     * @return mệnh đề AND hoặc chuỗi rỗng
     */
    public static String likeClause(String q, String... fields) {
        if (q == null || q.isBlank() || fields.length == 0) return "";
        StringBuilder sb = new StringBuilder(" AND (");
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) sb.append(" OR ");
            sb.append(fields[i]).append(" LIKE :q");
        }
        return sb.append(")").toString();
    }

    /**
     * Giá trị bind cho tham số :q của {@link #likeClause}.
     *
     * @param q từ khóa tìm kiếm
     * @return "%q%"
     */
    public static String likeParam(String q) {
        return "%" + q.trim() + "%";
    }

    /**
     * Parse chuỗi trạng thái từ client thành enum của module. Hỗ trợ các enum có hậu tố "_"
     * do trùng từ khóa Java (new → new_, return → return_). Không parse được → null (bỏ lọc).
     *
     * @param enumClass class enum trạng thái của module
     * @param value     chuỗi client gửi lên
     * @param <E>       kiểu enum
     * @return enum tương ứng hoặc null
     */
    public static <E extends Enum<E>> E parseEnum(Class<E> enumClass, String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException e) {
            try {
                return Enum.valueOf(enumClass, value + "_");
            } catch (IllegalArgumentException e2) {
                return null;
            }
        }
    }
}
