package vn.com.be_crm.core.audit;

/**
 * Đánh dấu Hibernate entity có hai cột audit {@code created_by} / {@code updated_by}.
 * {@link AuditInterceptor} chỉ đóng dấu cho entity implement interface này.
 *
 * <p>Entity chỉ cần khai {@code implements IAuditable} — Lombok {@code @Getter @Setter}
 * đã sinh sẵn đủ 5 method, không phải viết thêm gì.
 */
public interface IAuditable {

    /** @return ID bản ghi (null nghĩa là bản ghi mới, chưa insert) */
    Long getId();

    /** @return ID người tạo bản ghi */
    Long getCreatedBy();

    /** @param createdBy ID người tạo bản ghi */
    void setCreatedBy(Long createdBy);

    /** @return ID người sửa bản ghi gần nhất */
    Long getUpdatedBy();

    /** @param updatedBy ID người sửa bản ghi gần nhất */
    void setUpdatedBy(Long updatedBy);
}
