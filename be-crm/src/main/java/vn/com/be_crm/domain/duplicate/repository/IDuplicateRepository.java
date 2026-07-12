package vn.com.be_crm.domain.duplicate.repository;

import vn.com.be_crm.application.duplicate.dto.DuplicateMatch;

import java.util.List;

/**
 * Port dò bản ghi trùng theo email / số điện thoại / mã số thuế (chỉ đọc, native SQL).
 * Quét cả 3 phân hệ đầu phễu: tiềm năng, khách hàng, liên hệ.
 */
public interface IDuplicateRepository {

    /**
     * Tìm bản ghi có cùng email/SĐT/MST với dữ liệu đang nhập.
     *
     * @param email         email cần dò (null/rỗng = bỏ qua)
     * @param phone         số điện thoại cần dò (null/rỗng = bỏ qua)
     * @param taxCode       mã số thuế cần dò (null/rỗng = bỏ qua)
     * @param excludeModule phân hệ của bản ghi đang sửa (bỏ chính nó ra khỏi kết quả) — có thể null
     * @param excludeId     ID bản ghi đang sửa — có thể null
     * @return danh sách bản ghi nghi trùng (tối đa 5 mỗi phân hệ)
     */
    List<DuplicateMatch> find(String email, String phone, String taxCode, String excludeModule, Long excludeId);
}
