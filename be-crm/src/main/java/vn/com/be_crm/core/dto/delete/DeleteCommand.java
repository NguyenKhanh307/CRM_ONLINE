package vn.com.be_crm.core.dto.delete;

/**
 * Command dùng chung cho thao tác xóa mềm, truyền thêm ID người thực hiện.
 *
 * @param id        ID bản ghi cần xóa
 * @param deletedBy ID người dùng thực hiện xóa
 */
public record DeleteCommand(Long id, Long deletedBy) {}
