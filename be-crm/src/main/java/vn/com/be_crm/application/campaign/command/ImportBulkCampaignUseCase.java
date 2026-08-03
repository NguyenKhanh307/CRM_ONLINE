package vn.com.be_crm.application.campaign.command;

import vn.com.be_crm.application.campaign.dto.ImportBulkCampaignCommand;
import vn.com.be_crm.application.campaign.dto.ImportCampaignRowCommand;
import vn.com.be_crm.core.dto.import_.ImportBulkResult;
import vn.com.be_crm.core.dto.import_.ImportRowError;
import vn.com.be_crm.domain.campaign.entity.Campaign;
import vn.com.be_crm.domain.campaign.enums.CampaignStatus;
import vn.com.be_crm.domain.campaign.enums.CampaignType;
import vn.com.be_crm.domain.campaign.repository.ICampaignRepository;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Use case nhập hàng loạt Campaign từ file Excel/CSV. */
public class ImportBulkCampaignUseCase {
    private final ICampaignRepository repo;

    /** @param repo port lưu trữ */
    public ImportBulkCampaignUseCase(ICampaignRepository repo) { this.repo = repo; }

    /**
     * Xử lý nhập hàng loạt Campaign.
     * @param cmd dữ liệu import @return kết quả nhập
     */
    public ImportBulkResult execute(ImportBulkCampaignCommand cmd) {
        List<ImportRowError> errors = new ArrayList<>();
        int success = 0;
        for (int i = 0; i < cmd.rows().size(); i++) {
            int rowNum = i + 2;
            ImportCampaignRowCommand row = cmd.rows().get(i);
            try {
                Long ownerId = "SPECIFIC".equals(cmd.ownerMode()) ? cmd.specificOwnerId() : null;
                CampaignType type = parseEnum(CampaignType.class, row.type(), CampaignType.other);
                CampaignStatus status = parseEnum(CampaignStatus.class, row.status(), CampaignStatus.draft);

                boolean isUpdate = "UPDATE".equals(cmd.importType()) || "BOTH".equals(cmd.importType());
                boolean isCreate = "CREATE".equals(cmd.importType()) || "BOTH".equals(cmd.importType());

                Optional<Campaign> existing = Optional.empty();
                if (isUpdate && row.code() != null && !row.code().isBlank())
                    existing = repo.findByCode(row.code());

                if (existing.isPresent()) {
                    Campaign e = existing.get();
                    repo.save(e.toBuilder()
                            .name(row.name() != null ? row.name() : e.getName())
                            .type(type).status(status)
                            .channel(row.channel() != null ? row.channel() : e.getChannel())
                            .startDate(parseDate(row.startDate()) != null ? parseDate(row.startDate()) : e.getStartDate())
                            .endDate(parseDate(row.endDate()) != null ? parseDate(row.endDate()) : e.getEndDate())
                            .budget(row.budget() != null ? row.budget() : e.getBudget())
                            .actualCost(row.actualCost() != null ? row.actualCost() : e.getActualCost())
                            .targetSize(row.targetSize() != null ? row.targetSize() : e.getTargetSize())
                            .expectedRevenue(row.expectedRevenue() != null ? row.expectedRevenue() : e.getExpectedRevenue())
                            .ownerId(ownerId != null ? ownerId : e.getOwnerId())
                            .description(row.note() != null ? row.note() : e.getDescription())
                            .build());
                    success++;
                } else if (isCreate) {
                    String code = (row.code() != null && !row.code().isBlank())
                            ? row.code() : "CD-" + System.currentTimeMillis() + "-" + rowNum;
                    repo.save(Campaign.builder()
                            .code(code).name(row.name() != null ? row.name() : code)
                            .type(type).status(status).channel(row.channel())
                            .startDate(parseDate(row.startDate())).endDate(parseDate(row.endDate()))
                            .budget(row.budget()).actualCost(row.actualCost())
                            .targetSize(row.targetSize()).expectedRevenue(row.expectedRevenue())
                            .ownerId(ownerId).description(row.note())
                            .build());
                    success++;
                }
            } catch (Exception ex) {
                errors.add(new ImportRowError(rowNum, ex.getMessage() != null ? ex.getMessage() : "Lỗi không xác định"));
            }
        }
        return new ImportBulkResult(success, errors.size(), errors);
    }

    private <E extends Enum<E>> E parseEnum(Class<E> cls, String val, E def) {
        if (val == null || val.isBlank()) return def;
        try { return Enum.valueOf(cls, val.trim().toLowerCase()); }
        catch (Exception e) { return def; }
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try { return LocalDate.parse(s.trim()); }
        catch (DateTimeParseException e) { return null; }
    }
}
