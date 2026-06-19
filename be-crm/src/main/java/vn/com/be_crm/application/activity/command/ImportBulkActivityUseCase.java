package vn.com.be_crm.application.activity.command;

import vn.com.be_crm.application.activity.dto.ImportActivityRowCommand;
import vn.com.be_crm.application.activity.dto.ImportBulkActivityCommand;
import vn.com.be_crm.application.shared.dto.ImportBulkResult;
import vn.com.be_crm.application.shared.dto.ImportRowError;
import vn.com.be_crm.domain.activity.entity.Activity;
import vn.com.be_crm.domain.activity.enums.ActivityStatus;
import vn.com.be_crm.domain.activity.enums.ActivityType;
import vn.com.be_crm.domain.activity.repository.IActivityRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/** Use case nhập hàng loạt Activity từ file Excel/CSV. */
public class ImportBulkActivityUseCase {
    private final IActivityRepository repo;

    /** @param repo port lưu trữ */
    public ImportBulkActivityUseCase(IActivityRepository repo) { this.repo = repo; }

    /**
     * Xử lý nhập hàng loạt Activity.
     * @param cmd dữ liệu import @return kết quả nhập
     */
    public ImportBulkResult execute(ImportBulkActivityCommand cmd) {
        List<ImportRowError> errors = new ArrayList<>();
        int success = 0;
        // Activity chỉ hỗ trợ tạo mới (không có khóa duy nhất nên không cập nhật)
        // Duyệt từng dòng; rowNum = i + 2 vì dòng 1 là header trong file Excel
        for (int i = 0; i < cmd.rows().size(); i++) {
            int rowNum = i + 2;
            ImportActivityRowCommand row = cmd.rows().get(i);
            try {
                // Bắt buộc có tiêu đề, thiếu thì ghi lỗi dòng và bỏ qua
                if (row.subject() == null || row.subject().isBlank()) {
                    errors.add(new ImportRowError(rowNum, "Trường 'Tiêu đề' là bắt buộc"));
                    continue;
                }
                // Xác định người phụ trách: gán cố định theo cấu hình hoặc null
                Long assignedUserId = "SPECIFIC".equals(cmd.ownerMode()) ? cmd.specificOwnerId() : null;
                // Parse enum, để null nếu rỗng/không hợp lệ rồi fallback mặc định khi build
                ActivityType type = parseType(row.type());
                ActivityStatus status = parseStatus(row.status());
                // Tạo mới hoạt động
                repo.save(Activity.builder()
                        .type(type != null ? type : ActivityType.task)
                        .subject(row.subject()).content(row.content())
                        .assignedUserId(assignedUserId)
                        .status(status != null ? status : ActivityStatus.planned)
                        .dueAt(parseDateTime(row.dueAt()))
                        .build());
                success++;
            } catch (Exception ex) {
                // Gom lỗi theo từng dòng, không hủy cả lô — các dòng hợp lệ vẫn được lưu
                errors.add(new ImportRowError(rowNum, ex.getMessage() != null ? ex.getMessage() : "Lỗi không xác định"));
            }
        }
        return new ImportBulkResult(success, errors.size(), errors);
    }

    private ActivityType parseType(String s) {
        if (s == null || s.isBlank()) return null;
        try { return ActivityType.valueOf(s.trim().toLowerCase()); }
        catch (Exception e) { return null; }
    }

    private ActivityStatus parseStatus(String s) {
        if (s == null || s.isBlank()) return null;
        try { return ActivityStatus.valueOf(s.trim().toLowerCase()); }
        catch (Exception e) { return null; }
    }

    private LocalDateTime parseDateTime(String s) {
        if (s == null || s.isBlank()) return null;
        try { return LocalDateTime.parse(s.trim()); }
        catch (DateTimeParseException e) { return null; }
    }
}
