package vn.com.be_crm.application.duplicate.dto;

/**
 * Một bản ghi bị nghi trùng với dữ liệu người dùng đang nhập.
 * Chỉ để CẢNH BÁO — không chặn lưu (khách hàng có thể dùng chung số tổng đài, email công ty...).
 *
 * @param module       phân hệ chứa bản ghi (lead/customer/contact)
 * @param id           ID bản ghi
 * @param code         mã bản ghi (liên hệ không có mã → null)
 * @param name         tên bản ghi
 * @param matchedField trường bị trùng: email | phone | taxCode
 * @param matchedValue giá trị bị trùng
 */
public record DuplicateMatch(
        String module,
        Long id,
        String code,
        String name,
        String matchedField,
        String matchedValue) {
}
