package vn.com.be_crm.domain.shared.exception;

/**
 * Ném khi không tìm thấy entity theo ID hoặc điều kiện tìm kiếm.
 */
public class NotFoundException extends DomainException {

    /**
     * @param entityName tên entity (vd: "Customer")
     * @param id         giá trị ID không tìm thấy
     */
    public NotFoundException(String entityName, Object id) {
        super(entityName + " không tìm thấy với id: " + id);
    }

    /**
     * @param message mô tả chi tiết
     */
    public NotFoundException(String message) {
        super(message);
    }
}
