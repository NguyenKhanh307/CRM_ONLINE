package vn.com.be_crm.application.activity.command;

import vn.com.be_crm.application.activity.dto.ImportActivityRowCommand;
import vn.com.be_crm.application.activity.dto.ImportBulkActivityCommand;
import vn.com.be_crm.core.dto.import_.ImportBulkResult;
import vn.com.be_crm.core.dto.import_.ImportRowError;
import vn.com.be_crm.core.tx.port.ITransactionRunner;
import vn.com.be_crm.domain.activity.entity.Activity;
import vn.com.be_crm.domain.activity.enums.ActivityStatus;
import vn.com.be_crm.domain.activity.enums.ActivityType;
import vn.com.be_crm.domain.activity.repository.IActivityRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

// nhập hàng loạt Activity từ file Excel/CSV — validate TOÀN BỘ trước (pass 1, không đụng DB),
// chỉ khi sạch hết mới lưu TOÀN BỘ trong 1 transaction (pass 2)
public class ImportBulkActivityUseCase {
    private final IActivityRepository repo;
    private final ITransactionRunner txRunner;

    /** @param repo port lưu trữ @param txRunner chạy pass 2 trong 1 transaction */
    public ImportBulkActivityUseCase(IActivityRepository repo, ITransactionRunner txRunner) {
        this.repo = repo;
        this.txRunner = txRunner;
    }

    /**
     * Xử lý nhập hàng loạt Activity.
     *
     * @param cmd dữ liệu import @return kết quả nhập
     */
    public ImportBulkResult execute(ImportBulkActivityCommand cmd) {
        List<ImportRowError> errors = new ArrayList<>();

        // ----- Pass 1: validate mọi dòng, không chạm DB -----
        for (int i = 0; i < cmd.rows().size(); i++) {
            int rowNum = i + 2;
            ImportActivityRowCommand row = cmd.rows().get(i);
            if (row.subject() == null || row.subject().isBlank()) {
                errors.add(new ImportRowError(rowNum, "Trường 'Tiêu đề' là bắt buộc"));
                continue;
            }
            String err = null;
            if (row.type() != null && !row.type().isBlank() && parseType(row.type()) == null) {
                err = "Trường \"Loại hoạt động\" giá trị không hợp lệ: \"" + row.type() + "\"";
            }
            if (err == null && row.status() != null && !row.status().isBlank() && parseStatus(row.status()) == null) {
                err = "Trường \"Trạng thái\" giá trị không hợp lệ: \"" + row.status() + "\"";
            }
            if (err != null) errors.add(new ImportRowError(rowNum, err));
        }
        if (!errors.isEmpty()) return new ImportBulkResult(0, errors.size(), errors);

        // ----- Pass 2: mọi dòng đã sạch — lưu toàn bộ trong 1 transaction -----
        // Activity chỉ hỗ trợ tạo mới (không có khóa duy nhất nên không cập nhật)
        int[] success = {0};
        try {
            txRunner.run(() -> {
                for (int i = 0; i < cmd.rows().size(); i++) {
                    ImportActivityRowCommand row = cmd.rows().get(i);
                    // Xác định người phụ trách: gán cố định theo cấu hình hoặc null
                    Long assignedUserId = "SPECIFIC".equals(cmd.ownerMode()) ? cmd.specificOwnerId() : null;
                    ActivityType type = parseType(row.type());
                    ActivityStatus status = parseStatus(row.status());
                    repo.save(Activity.builder()
                            .type(type != null ? type : ActivityType.task)
                            .subject(row.subject()).content(row.content())
                            .priority(row.priority())
                            .targetType(row.targetType()).targetId(row.targetId())
                            .location(row.location())
                            .callDirection(row.callDirection()).callResult(row.callResult())
                            .callDuration(row.callDuration())
                            .assignedUserId(assignedUserId)
                            .status(status != null ? status : ActivityStatus.planned)
                            .dueAt(parseDateTime(row.dueAt()))
                            .build());
                    success[0]++;
                }
            });
        } catch (Exception ex) {
            return new ImportBulkResult(0, 1, List.of(new ImportRowError(0,
                    ex.getMessage() != null ? ex.getMessage() : "Lỗi không xác định khi lưu dữ liệu")));
        }
        return new ImportBulkResult(success[0], 0, List.of());
    }

    // Các hàm parse enum/dateTime, trả về null nếu rỗng/không hợp lệ
    private ActivityType parseType(String s) {
        if (s == null || s.isBlank())
            return null;
        try {
            return ActivityType.valueOf(s.trim().toLowerCase());
        } catch (Exception e) {
            return null;
        }
    }

    private ActivityStatus parseStatus(String s) {
        if (s == null || s.isBlank())
            return null;
        try {
            return ActivityStatus.valueOf(s.trim().toLowerCase());
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDateTime parseDateTime(String s) {
        if (s == null || s.isBlank())
            return null;
        // Wizard gửi ISO date thuần (yyyy-MM-dd); chấp nhận cả datetime đầy đủ nếu có
        try {
            return java.time.LocalDate.parse(s.trim()).atStartOfDay();
        } catch (DateTimeParseException e1) {
            try {
                return LocalDateTime.parse(s.trim());
            } catch (DateTimeParseException e2) {
                return null;
            }
        }
    }
}
