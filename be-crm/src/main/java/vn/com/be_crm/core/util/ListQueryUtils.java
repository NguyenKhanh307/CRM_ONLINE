package vn.com.be_crm.core.util;

/**
 * Tiện ích dựng mệnh đề HQL động cho các list query (owner filter, search LIKE,
 * sort an toàn).
 * Dùng chung cho mọi *RepositoryImpl.findAll.
 */
public final class ListQueryUtils {

    private ListQueryUtils() {
    }

    // chuẩn hóa tên field sắp xếp — chỉ nhận chữ cái, số, gạch dưới, bắt đầu bằng
    // chữ cái, tối đa 50 ký tự.
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
     * Dựng mệnh đề search LIKE trên nhiều field: " AND (f1 LIKE :q OR f2 LIKE :q
     * ...)".
     * Trả chuỗi rỗng khi không có từ khóa. Caller phải bind tham số :q =
     * likeParam(q).
     *
     * @param q      từ khóa tìm kiếm (có thể null)
     * @param fields danh sách tên field HQL để so LIKE
     * @return mệnh đề AND hoặc chuỗi rỗng
     */
    // vd likeClause("abc", "f1", "f2") → " AND (f1 LIKE :q OR f2 LIKE :q)"
    public static String likeClause(String q, String... fields) {
        // trường hợp q null hoặc rỗng, hoặc không có field nào → bỏ lọc
        if (q == null || q.isBlank() || fields.length == 0)
            return "";
        StringBuilder sb = new StringBuilder(" AND (");
        for (int i = 0; i < fields.length; i++) {
            if (i > 0)
                sb.append(" OR ");
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
    // vd likeParam("abc") → "%abc%"
    public static String likeParam(String q) {
        return "%" + q.trim() + "%";
    }

    // ----- Bản tìm kiếm CHÍNH XÁC (đang tắt, mở khi cần dùng) -----
    // /**
    // * Dựng mệnh đề search khớp CHÍNH XÁC trên nhiều field: " AND (f1 = :q OR
    // * f2 = :q ...)".
    // * Trả chuỗi rỗng khi không có từ khóa. Caller phải bind tham số :q =
    // * exactParam(q).
    // *
    // * @param q từ khóa tìm kiếm (có thể null)
    // * @param fields danh sách tên field HQL để so khớp chính xác
    // * @return mệnh đề AND hoặc chuỗi rỗng
    // */
    // // vd exactClause("abc", "f1", "f2") → " AND (f1 = :q OR f2 = :q)"
    // public static String exactClause(String q, String... fields) {
    // if (q == null || q.isBlank() || fields.length == 0)
    // return "";
    // StringBuilder sb = new StringBuilder(" AND (");
    // for (int i = 0; i < fields.length; i++) {
    // if (i > 0)
    // sb.append(" OR ");
    // sb.append(fields[i]).append(" = :q");
    // }
    // return sb.append(")").toString();
    // }
    //
    // /**
    // * Giá trị bind cho tham số :q của exactClause.
    // *
    // * @param q từ khóa tìm kiếm
    // * @return q đã trim, không bọc wildcard
    // */
    // // vd exactParam(" abc ") → "abc"
    // public static String exactParam(String q) {
    // return q.trim();
    // }

    /**
     * Parse chuỗi trạng thái từ client thành enum của module. Hỗ trợ các enum có
     * hậu tố "_"
     * do trùng từ khóa Java (new → new_, return → return_). Không parse được → null
     * (bỏ lọc).
     *
     * @param enumClass class enum trạng thái của module
     * @param value     chuỗi client gửi lên
     * @param <E>       kiểu enum
     * @return enum tương ứng hoặc null
     */
    public static <E extends Enum<E>> E parseEnum(Class<E> enumClass, String value) {
        if (value == null || value.isBlank())
            return null;
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
