package vn.com.be_crm.application.opportunity.command;

import vn.com.be_crm.application.opportunity.dto.ImportBulkOpportunityCommand;
import vn.com.be_crm.application.opportunity.dto.ImportOpportunityRowCommand;
import vn.com.be_crm.application.shared.dto.ImportBulkResult;
import vn.com.be_crm.application.shared.dto.ImportRowError;
import vn.com.be_crm.domain.opportunity.entity.Opportunity;
import vn.com.be_crm.domain.opportunity.enums.OpportunityStatus;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Use case nhập hàng loạt Opportunity từ file Excel/CSV. */
public class ImportBulkOpportunityUseCase {
    private final IOpportunityRepository repo;

    /** @param repo port lưu trữ */
    public ImportBulkOpportunityUseCase(IOpportunityRepository repo) { this.repo = repo; }

    /**
     * Xử lý nhập hàng loạt Opportunity.
     * @param cmd dữ liệu import @return kết quả nhập
     */
    public ImportBulkResult execute(ImportBulkOpportunityCommand cmd) {
        List<ImportRowError> errors = new ArrayList<>();
        int success = 0;
        // Duyệt từng dòng; rowNum = i + 2 vì dòng 1 là header trong file Excel
        for (int i = 0; i < cmd.rows().size(); i++) {
            int rowNum = i + 2;
            ImportOpportunityRowCommand row = cmd.rows().get(i);
            try {
                if (row.name() == null || row.name().isBlank()) {
                    // Gom lỗi theo từng dòng, không hủy cả lô — các dòng hợp lệ vẫn được lưu
                    errors.add(new ImportRowError(rowNum, "Trường 'Tên cơ hội' là bắt buộc"));
                    continue;
                }
                // Xác định owner: gán cố định theo cấu hình hoặc null (lấy từ dòng/bản ghi cũ)
                Long ownerId = "SPECIFIC".equals(cmd.ownerMode()) ? cmd.specificOwnerId() : null;
                OpportunityStatus status = parseStatus(row.status());
                LocalDate closeDate = parseDate(row.expectedCloseDate());

                // Xác định nhánh thao tác theo importType: CREATE / UPDATE / BOTH
                boolean isUpdate = "UPDATE".equals(cmd.importType()) || "BOTH".equals(cmd.importType());
                boolean isCreate = "CREATE".equals(cmd.importType()) || "BOTH".equals(cmd.importType());

                // Tìm bản ghi trùng theo khóa duy nhất để cập nhật (chỉ khi cho phép UPDATE)
                Optional<Opportunity> existing = Optional.empty();
                if (isUpdate && row.code() != null && !row.code().isBlank())
                    existing = repo.findByCode(row.code());

                // Có bản ghi → cập nhật (giữ field cũ, ghi đè field có trong file)
                if (existing.isPresent()) {
                    Opportunity e = existing.get();
                    repo.save(Opportunity.builder()
                            .id(e.getId()).code(e.getCode()).name(row.name())
                            .opportunityType(e.getOpportunityType())
                            .customerId(e.getCustomerId()).contactId(e.getContactId())
                            .ownerId(ownerId != null ? ownerId : e.getOwnerId()).stageId(e.getStageId())
                            .amount(row.amount() != null ? row.amount() : e.getAmount())
                            .expectedRevenue(e.getExpectedRevenue())
                            .probability(row.probability() != null ? row.probability() : e.getProbability())
                            .expectedCloseDate(closeDate != null ? closeDate : e.getExpectedCloseDate())
                            .source(e.getSource()).winLossReason(e.getWinLossReason()).description(e.getDescription())
                            .status(status != null ? status : e.getStatus())
                            .createdAt(e.getCreatedAt()).build());
                    success++;
                // Chưa có và được phép tạo mới → thêm mới
                } else if (isCreate) {
                    String code = "CO-" + System.currentTimeMillis() + "-" + rowNum;
                    repo.save(Opportunity.builder()
                            .code(code).name(row.name()).ownerId(ownerId)
                            .amount(row.amount()).probability(row.probability())
                            .expectedCloseDate(closeDate)
                            .status(status != null ? status : OpportunityStatus.open)
                            .build());
                    success++;
                }
            } catch (Exception ex) {
                // Gom lỗi theo từng dòng, không hủy cả lô — các dòng hợp lệ vẫn được lưu
                errors.add(new ImportRowError(rowNum, ex.getMessage() != null ? ex.getMessage() : "Lỗi không xác định"));
            }
        }
        return new ImportBulkResult(success, errors.size(), errors);
    }

    private OpportunityStatus parseStatus(String s) {
        if (s == null || s.isBlank()) return null;
        try { return OpportunityStatus.valueOf(s.trim().toLowerCase()); }
        catch (Exception e) { return null; }
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try { return LocalDate.parse(s.trim()); }
        catch (DateTimeParseException e) { return null; }
    }
}
