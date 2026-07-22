package vn.com.be_crm.application.copilot.dto;

/**
 * Tham chiếu gọn tới một bản ghi tìm được theo tên/mã (phục vụ lệnh "mở <module> <tên>").
 *
 * @param id   ID bản ghi
 * @param code mã bản ghi (có thể null với bảng không có code)
 * @param name tên hiển thị
 */
public record RecordRef(long id, String code, String name) {
}
