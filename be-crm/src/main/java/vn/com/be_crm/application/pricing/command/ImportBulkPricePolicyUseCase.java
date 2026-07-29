package vn.com.be_crm.application.pricing.command;

import vn.com.be_crm.application.pricing.dto.ImportBulkPricePolicyCommand;
import vn.com.be_crm.application.pricing.dto.ImportPricePolicyRowCommand;
import vn.com.be_crm.application.shared.dto.ImportBulkResult;
import vn.com.be_crm.application.shared.dto.ImportRowError;
import vn.com.be_crm.domain.pricing.entity.PricePolicy;
import vn.com.be_crm.domain.pricing.enums.PricePolicyStatus;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyRepository;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Use case nhập hàng loạt PricePolicy (chính sách giá) từ file Excel/CSV — chỉ bảng header. */
public class ImportBulkPricePolicyUseCase {
    private final IPricePolicyRepository repo;

    /** @param repo port lưu trữ */
    public ImportBulkPricePolicyUseCase(IPricePolicyRepository repo) { this.repo = repo; }

    /**
     * Xử lý nhập hàng loạt PricePolicy.
     * @param cmd dữ liệu import @return kết quả nhập
     */
    public ImportBulkResult execute(ImportBulkPricePolicyCommand cmd) {
        List<ImportRowError> errors = new ArrayList<>();
        int success = 0;
        for (int i = 0; i < cmd.rows().size(); i++) {
            int rowNum = i + 2;
            ImportPricePolicyRowCommand row = cmd.rows().get(i);
            try {
                if (row.name() == null || row.name().isBlank()) {
                    errors.add(new ImportRowError(rowNum, "Trường 'Tên chính sách' là bắt buộc"));
                    continue;
                }
                PricePolicyStatus status = parseStatus(row.status());
                LocalDate startDate = parseDate(row.startDate());
                LocalDate endDate = parseDate(row.endDate());

                boolean isUpdate = "UPDATE".equals(cmd.importType()) || "BOTH".equals(cmd.importType());
                boolean isCreate = "CREATE".equals(cmd.importType()) || "BOTH".equals(cmd.importType());

                Optional<PricePolicy> existing = Optional.empty();
                if (isUpdate && row.code() != null && !row.code().isBlank())
                    existing = repo.findByCode(row.code());

                if (existing.isPresent()) {
                    PricePolicy e = existing.get();
                    repo.save(PricePolicy.builder()
                            .id(e.getId()).code(e.getCode()).name(row.name())
                            .type(row.type() != null ? row.type() : e.getType())
                            .priority(row.priority() != null ? row.priority() : e.getPriority())
                            .startDate(startDate != null ? startDate : e.getStartDate())
                            .endDate(endDate != null ? endDate : e.getEndDate())
                            .status(status != null ? status : e.getStatus())
                            .createdBy(e.getCreatedBy()).createdAt(e.getCreatedAt())
                            .build());
                    success++;
                } else if (isCreate) {
                    String code = (row.code() != null && !row.code().isBlank())
                            ? row.code() : "CSG-" + System.currentTimeMillis() + "-" + rowNum;
                    repo.save(PricePolicy.builder()
                            .code(code).name(row.name()).type(row.type())
                            .priority(row.priority() != null ? row.priority() : 0)
                            .startDate(startDate).endDate(endDate)
                            .status(status != null ? status : PricePolicyStatus.active)
                            .build());
                    success++;
                }
            } catch (Exception ex) {
                errors.add(new ImportRowError(rowNum, ex.getMessage() != null ? ex.getMessage() : "Lỗi không xác định"));
            }
        }
        return new ImportBulkResult(success, errors.size(), errors);
    }

    private PricePolicyStatus parseStatus(String s) {
        if (s == null || s.isBlank()) return null;
        try { return PricePolicyStatus.valueOf(s.trim().toLowerCase()); }
        catch (Exception e) { return null; }
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try { return LocalDate.parse(s.trim()); }
        catch (DateTimeParseException e) { return null; }
    }
}
