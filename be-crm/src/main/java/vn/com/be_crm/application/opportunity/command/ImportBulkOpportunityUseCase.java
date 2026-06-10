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
        for (int i = 0; i < cmd.rows().size(); i++) {
            int rowNum = i + 2;
            ImportOpportunityRowCommand row = cmd.rows().get(i);
            try {
                if (row.name() == null || row.name().isBlank()) {
                    errors.add(new ImportRowError(rowNum, "Trường 'Tên cơ hội' là bắt buộc"));
                    continue;
                }
                Long ownerId = "SPECIFIC".equals(cmd.ownerMode()) ? cmd.specificOwnerId() : null;
                String code = "CO-" + System.currentTimeMillis() + "-" + rowNum;
                OpportunityStatus status = parseStatus(row.status());
                LocalDate closeDate = parseDate(row.expectedCloseDate());
                repo.save(Opportunity.builder()
                        .code(code).name(row.name()).ownerId(ownerId)
                        .amount(row.amount()).probability(row.probability())
                        .expectedCloseDate(closeDate)
                        .status(status != null ? status : OpportunityStatus.open)
                        .build());
                success++;
            } catch (Exception ex) {
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
