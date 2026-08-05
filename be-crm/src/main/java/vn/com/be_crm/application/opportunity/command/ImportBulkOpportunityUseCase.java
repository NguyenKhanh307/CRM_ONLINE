package vn.com.be_crm.application.opportunity.command;

import vn.com.be_crm.application.opportunity.dto.ImportBulkOpportunityCommand;
import vn.com.be_crm.application.opportunity.dto.ImportOpportunityRowCommand;
import vn.com.be_crm.core.dto.import_.ImportBulkResult;
import vn.com.be_crm.core.dto.import_.ImportRowError;
import vn.com.be_crm.domain.opportunity.entity.Opportunity;
import vn.com.be_crm.domain.opportunity.enums.OpportunityStatus;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// nhập hàng loạt Opportunity từ file Excel/CSV — mỗi dòng xử lý độc lập, lỗi từng dòng được
// thu thập, không dừng toàn bộ lô
public class ImportBulkOpportunityUseCase {
    private final IOpportunityRepository repo;

    public ImportBulkOpportunityUseCase(IOpportunityRepository repo) { this.repo = repo; }

    public ImportBulkResult execute(ImportBulkOpportunityCommand cmd) {
        List<ImportRowError> errors = new ArrayList<>();
        int success = 0;
        for (int i = 0; i < cmd.rows().size(); i++) {
            int rowNum = i + 2; // dòng 1 là header trong file Excel
            ImportOpportunityRowCommand row = cmd.rows().get(i);
            try {
                if (row.name() == null || row.name().isBlank()) {
                    errors.add(new ImportRowError(rowNum, "Trường 'Tên cơ hội' là bắt buộc"));
                    continue;
                }
                Long ownerId = "SPECIFIC".equals(cmd.ownerMode()) ? cmd.specificOwnerId() : null;
                OpportunityStatus status = parseStatus(row.status());

                boolean isUpdate = "UPDATE".equals(cmd.importType()) || "BOTH".equals(cmd.importType());
                boolean isCreate = "CREATE".equals(cmd.importType()) || "BOTH".equals(cmd.importType());

                // tìm bản ghi trùng theo khóa duy nhất để cập nhật (chỉ khi cho phép UPDATE)
                Optional<Opportunity> existing = Optional.empty();
                if (isUpdate && row.code() != null && !row.code().isBlank())
                    existing = repo.findByCode(row.code());

                // có bản ghi -> cập nhật (giữ field cũ, ghi đè field có trong file)
                if (existing.isPresent()) {
                    Opportunity e = existing.get();
                    repo.save(Opportunity.builder()
                            .id(e.getId()).code(e.getCode()).name(row.name())
                            .opportunityType(row.opportunityType() != null ? row.opportunityType() : e.getOpportunityType())
                            .customerId(row.customerId() != null ? row.customerId() : e.getCustomerId())
                            .contactId(row.contactId() != null ? row.contactId() : e.getContactId())
                            .ownerId(ownerId != null ? ownerId : e.getOwnerId())
                            .stageId(row.stageId() != null ? row.stageId() : e.getStageId())
                            .pricePolicyId(row.pricePolicyId() != null ? row.pricePolicyId() : e.getPricePolicyId())
                            .amount(row.amount() != null ? row.amount() : e.getAmount())
                            .source(row.source() != null ? row.source() : e.getSource())
                            .campaignId(row.campaignId() != null ? row.campaignId() : e.getCampaignId())
                            .winLossReason(row.winLossReason() != null ? row.winLossReason() : e.getWinLossReason())
                            .description(row.description() != null ? row.description() : e.getDescription())
                            .status(status != null ? status : e.getStatus())
                            .createdAt(e.getCreatedAt()).build());
                    success++;
                // chưa có và được phép tạo mới -> thêm mới
                } else if (isCreate) {
                    String code = "CO-" + System.currentTimeMillis() + "-" + rowNum;
                    repo.save(Opportunity.builder()
                            .code(code).name(row.name()).ownerId(ownerId)
                            .opportunityType(row.opportunityType())
                            .customerId(row.customerId()).contactId(row.contactId())
                            .stageId(row.stageId()).pricePolicyId(row.pricePolicyId())
                            .amount(row.amount() != null ? row.amount() : java.math.BigDecimal.ZERO)
                            .source(row.source()).campaignId(row.campaignId())
                            .winLossReason(row.winLossReason()).description(row.description())
                            .status(status != null ? status : OpportunityStatus.open)
                            .build());
                    success++;
                }
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
}
