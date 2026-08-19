package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.lead.dto.ImportBulkLeadCommand;
import vn.com.be_crm.application.lead.dto.ImportLeadRowCommand;
import vn.com.be_crm.core.dto.import_.ImportBulkResult;
import vn.com.be_crm.core.dto.import_.ImportRowError;
import vn.com.be_crm.core.tx.port.ITransactionRunner;
import vn.com.be_crm.core.util.ImportValidators;
import vn.com.be_crm.domain.lead.entity.Lead;
import vn.com.be_crm.domain.lead.enums.LeadStatus;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// nhập hàng loạt Lead từ file Excel/CSV — validate TOÀN BỘ trước (pass 1, không đụng DB), chỉ khi
// sạch hết mới lưu TOÀN BỘ trong 1 transaction (pass 2) — file có 1 dòng lỗi thì không dòng nào
// được nhập, tránh nhập nửa mùa rồi người dùng phải dò xem dòng nào đã vào DB
public class ImportBulkLeadUseCase {
    private final ILeadRepository repo;
    private final ITransactionRunner txRunner;

    public ImportBulkLeadUseCase(ILeadRepository repo, ITransactionRunner txRunner) {
        this.repo = repo;
        this.txRunner = txRunner;
    }

    public ImportBulkResult execute(ImportBulkLeadCommand cmd) {
        List<ImportRowError> errors = new ArrayList<>();

        // ----- Pass 1: validate mọi dòng, không chạm DB -----
        for (int i = 0; i < cmd.rows().size(); i++) {
            int rowNum = i + 2; // dòng 1 là header trong file Excel
            ImportLeadRowCommand row = cmd.rows().get(i);
            if (row.name() == null || row.name().isBlank()) {
                errors.add(new ImportRowError(rowNum, "Trường 'Họ và tên' là bắt buộc"));
                continue;
            }
            String err = ImportValidators.emailError(row.email());
            if (err == null) err = ImportValidators.phoneError(row.phone());
            if (err == null && row.status() != null && !row.status().isBlank() && parseStatus(row.status()) == null) {
                err = "Trường \"Trạng thái\" giá trị không hợp lệ: \"" + row.status() + "\"";
            }
            if (err != null) errors.add(new ImportRowError(rowNum, err));
        }
        if (!errors.isEmpty()) return new ImportBulkResult(0, errors.size(), errors);

        // ----- Pass 2: mọi dòng đã sạch — lưu toàn bộ trong 1 transaction -----
        int[] success = {0};
        try {
            txRunner.run(() -> {
                for (int i = 0; i < cmd.rows().size(); i++) {
                    int rowNum = i + 2;
                    ImportLeadRowCommand row = cmd.rows().get(i);
                    Long ownerId = resolveOwnerId(cmd, row);
                    LeadStatus status = parseStatus(row.status());

                    boolean isUpdate = "UPDATE".equals(cmd.importType()) || "BOTH".equals(cmd.importType());
                    boolean isCreate = "CREATE".equals(cmd.importType()) || "BOTH".equals(cmd.importType());

                    // tìm bản ghi trùng theo khóa duy nhất để cập nhật (chỉ khi cho phép UPDATE)
                    Optional<Lead> existing = Optional.empty();
                    if (isUpdate) {
                        if (row.phone() != null && !row.phone().isBlank())
                            existing = repo.findByPhone(row.phone());
                        if (existing.isEmpty() && row.email() != null && !row.email().isBlank())
                            existing = repo.findByEmail(row.email());
                    }

                    // có bản ghi -> cập nhật (giữ field cũ, ghi đè field có trong file)
                    if (existing.isPresent()) {
                        Lead e = existing.get();
                        repo.save(Lead.builder()
                                .id(e.getId()).code(e.getCode())
                                .name(row.name()).ownerId(ownerId)
                                .contactId(e.getContactId())
                                .convertedOpportunityId(e.getConvertedOpportunityId())
                                .companyName(row.companyName() != null ? row.companyName() : e.getCompanyName())
                                .leadType(row.leadType() != null ? row.leadType() : e.getLeadType())
                                .taxCode(row.taxCode() != null ? row.taxCode() : e.getTaxCode())
                                .website(row.website() != null ? row.website() : e.getWebsite())
                                .industry(row.industry() != null ? row.industry() : e.getIndustry())
                                .campaignId(row.campaignId() != null ? row.campaignId() : e.getCampaignId())
                                .source(row.source() != null ? row.source() : e.getSource())
                                .status(status != null ? status : e.getStatus())
                                .phone(row.phone() != null ? row.phone() : e.getPhone())
                                .email(row.email() != null ? row.email() : e.getEmail())
                                .note(row.note() != null ? row.note() : e.getNote())
                                .createdAt(e.getCreatedAt()).build());
                        success[0]++;
                    // chưa có và được phép tạo mới -> thêm mới
                    } else if (isCreate) {
                        String code = "IMP-" + System.currentTimeMillis() + "-" + rowNum;
                        repo.save(Lead.builder()
                                .code(code).name(row.name()).ownerId(ownerId)
                                .companyName(row.companyName()).leadType(row.leadType())
                                .taxCode(row.taxCode())
                                .website(row.website()).industry(row.industry()).campaignId(row.campaignId())
                                .source(row.source())
                                .status(status != null ? status : LeadStatus.new_)
                                .phone(row.phone()).email(row.email())
                                .note(row.note())
                                .build());
                        success[0]++;
                    }
                }
            });
        } catch (Exception ex) {
            // lỗi bất ngờ ở tầng DB (vd trùng mã) -> rollback toàn bộ, không dòng nào được nhập
            return new ImportBulkResult(0, 1, List.of(new ImportRowError(0,
                    ex.getMessage() != null ? ex.getMessage() : "Lỗi không xác định khi lưu dữ liệu")));
        }
        return new ImportBulkResult(success[0], 0, List.of());
    }

    private Long resolveOwnerId(ImportBulkLeadCommand cmd, ImportLeadRowCommand row) {
        if ("SPECIFIC".equals(cmd.ownerMode())) return cmd.specificOwnerId();
        return null;
    }

    private LeadStatus parseStatus(String s) {
        if (s == null || s.isBlank()) return null;
        try { return LeadStatus.fromDb(s.trim().toLowerCase()); }
        catch (Exception e) { return null; }
    }
}
