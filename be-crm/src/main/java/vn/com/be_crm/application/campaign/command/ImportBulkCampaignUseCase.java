package vn.com.be_crm.application.campaign.command;

import vn.com.be_crm.application.campaign.dto.ImportBulkCampaignCommand;
import vn.com.be_crm.application.campaign.dto.ImportCampaignRowCommand;
import vn.com.be_crm.core.dto.import_.ImportBulkResult;
import vn.com.be_crm.core.dto.import_.ImportRowError;
import vn.com.be_crm.core.tx.port.ITransactionRunner;
import vn.com.be_crm.domain.campaign.entity.Campaign;
import vn.com.be_crm.domain.campaign.enums.CampaignStatus;
import vn.com.be_crm.domain.campaign.enums.CampaignType;
import vn.com.be_crm.domain.campaign.repository.ICampaignRepository;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

// nhập hàng loạt Campaign từ file Excel/CSV — validate TOÀN BỘ trước (pass 1, không đụng DB), chỉ
// khi sạch hết mới lưu TOÀN BỘ trong 1 transaction (pass 2)
public class ImportBulkCampaignUseCase {
    private final ICampaignRepository repo;
    private final ITransactionRunner txRunner;

    /** @param repo port lưu trữ @param txRunner chạy pass 2 trong 1 transaction */
    public ImportBulkCampaignUseCase(ICampaignRepository repo, ITransactionRunner txRunner) {
        this.repo = repo;
        this.txRunner = txRunner;
    }

    /**
     * Xử lý nhập hàng loạt Campaign.
     * @param cmd dữ liệu import @return kết quả nhập
     */
    public ImportBulkResult execute(ImportBulkCampaignCommand cmd) {
        List<ImportRowError> errors = new ArrayList<>();
        Set<String> seenCodes = new HashSet<>();

        // ----- Pass 1: validate mọi dòng, không chạm DB (trừ check trùng mã đọc-thôi bên dưới) -----
        for (int i = 0; i < cmd.rows().size(); i++) {
            int rowNum = i + 2;
            ImportCampaignRowCommand row = cmd.rows().get(i);
            String err = null;
            if (!isValidEnumOrBlank(CampaignType.class, row.type())) {
                err = "Trường \"Loại chiến dịch\" giá trị không hợp lệ: \"" + row.type() + "\"";
            }
            if (err == null && !isValidEnumOrBlank(CampaignStatus.class, row.status())) {
                err = "Trường \"Trạng thái\" giá trị không hợp lệ: \"" + row.status() + "\"";
            }
            if (err != null) errors.add(new ImportRowError(rowNum, err));

            // check trùng mã khi tạo mới thuần — tránh crash cả file do vi phạm unique constraint ở Pass 2
            if ("CREATE".equals(cmd.importType()) && row.code() != null && !row.code().isBlank()) {
                String code = row.code().trim();
                if (!seenCodes.add(code)) {
                    errors.add(new ImportRowError(rowNum, "Mã \"" + code + "\" bị trùng với dòng khác trong file"));
                } else if (repo.findByCode(code).isPresent()) {
                    errors.add(new ImportRowError(rowNum, "Mã \"" + code + "\" đã tồn tại, vui lòng dùng mã khác hoặc chọn chế độ Cập nhật"));
                }
            }
        }
        if (!errors.isEmpty()) return new ImportBulkResult(0, errors.size(), errors);

        // ----- Pass 2: mọi dòng đã sạch — lưu toàn bộ trong 1 transaction -----
        int[] success = {0};
        try {
            txRunner.run(() -> {
                for (int i = 0; i < cmd.rows().size(); i++) {
                    int rowNum = i + 2;
                    ImportCampaignRowCommand row = cmd.rows().get(i);
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
                        success[0]++;
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
                        success[0]++;
                    }
                }
            });
        } catch (Exception ex) {
            return new ImportBulkResult(0, 1, List.of(new ImportRowError(0,
                    ex.getMessage() != null ? ex.getMessage() : "Lỗi không xác định khi lưu dữ liệu")));
        }
        return new ImportBulkResult(success[0], 0, List.of());
    }

    private <E extends Enum<E>> E parseEnum(Class<E> cls, String val, E def) {
        if (val == null || val.isBlank()) return def;
        try { return Enum.valueOf(cls, val.trim().toLowerCase()); }
        catch (Exception e) { return def; }
    }

    // rỗng thì hợp lệ (dùng mặc định); có giá trị thì phải khớp đúng enum
    private <E extends Enum<E>> boolean isValidEnumOrBlank(Class<E> cls, String val) {
        if (val == null || val.isBlank()) return true;
        try { Enum.valueOf(cls, val.trim().toLowerCase()); return true; }
        catch (Exception e) { return false; }
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try { return LocalDate.parse(s.trim()); }
        catch (DateTimeParseException e) { return null; }
    }
}
