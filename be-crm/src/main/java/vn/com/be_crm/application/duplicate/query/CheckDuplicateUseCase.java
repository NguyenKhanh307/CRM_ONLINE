package vn.com.be_crm.application.duplicate.query;

import vn.com.be_crm.application.duplicate.dto.DuplicateMatch;
import vn.com.be_crm.domain.duplicate.repository.IDuplicateRepository;

import java.util.List;

/**
 * Use case dò bản ghi trùng email/SĐT/MST khi người dùng đang nhập tiềm năng/khách hàng/liên hệ,
 * và khi convert tiềm năng (để mời dùng lại khách hàng đã có thay vì tạo trùng).
 * Kết quả chỉ để CẢNH BÁO — không chặn lưu.
 */
public class CheckDuplicateUseCase {

    private final IDuplicateRepository repo;

    /** @param repo port dò trùng */
    public CheckDuplicateUseCase(IDuplicateRepository repo) {
        this.repo = repo;
    }

    /**
     * @param email         email đang nhập
     * @param phone         số điện thoại đang nhập
     * @param taxCode       mã số thuế đang nhập
     * @param excludeModule phân hệ của bản ghi đang sửa (bỏ chính nó khỏi kết quả)
     * @param excludeId     ID bản ghi đang sửa
     * @return danh sách bản ghi nghi trùng
     */
    public List<DuplicateMatch> execute(String email, String phone, String taxCode,
                                        String excludeModule, Long excludeId) {
        return repo.find(email, phone, taxCode, excludeModule, excludeId);
    }
}
