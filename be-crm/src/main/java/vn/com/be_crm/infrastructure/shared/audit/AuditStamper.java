package vn.com.be_crm.infrastructure.shared.audit;

/**
 * Đóng dấu người tạo/người sửa lên Hibernate entity NGAY TRƯỚC khi {@code merge}, gọi ở cuối mỗi
 * {@code *HibernateMapper.toHibernate()}.
 *
 * <p>Vì sao cần, khi đã có {@link AuditInterceptor}? Repo chạy
 * {@code mapper.toDomain(s.merge(...))} — tức là đọc entity ra <b>trước</b> khi commit, trong khi
 * {@code onFlushDirty} chỉ chạy <b>lúc flush/commit</b>. Không có lớp này thì DB đúng nhưng
 * body JSON của {@code PUT} trả về {@code createdBy: null} và {@code updatedBy} cũ.
 */
public final class AuditStamper {

    private AuditStamper() {}

    /**
     * @param h                hibernate entity sắp merge
     * @param domainCreatedBy  createdBy đọc từ domain entity (đã load từ DB khi cập nhật)
     * @param domainUpdatedBy  updatedBy đọc từ domain entity
     * @param <T>              kiểu hibernate entity
     * @return chính {@code h}, đã đóng dấu
     */
    public static <T extends IAuditable> T stamp(T h, Long domainCreatedBy, Long domainUpdatedBy) {
        Long userId = CurrentUserHolder.get();
        boolean isNew = h.getId() == null;

        // Tạo mới → người đang thao tác; cập nhật → giữ nguyên người tạo cũ (KHÔNG được để null)
        h.setCreatedBy(isNew ? userId : domainCreatedBy);
        // Luôn là người đang thao tác; endpoint public (không có user) → giữ giá trị cũ
        h.setUpdatedBy(userId != null ? userId : domainUpdatedBy);
        return h;
    }
}
