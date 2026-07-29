package vn.com.be_crm.application.lead.command;

import vn.com.be_crm.application.lead.dto.ImportBulkLeadCommand;
import vn.com.be_crm.application.lead.dto.ImportLeadRowCommand;
import vn.com.be_crm.application.shared.dto.ImportBulkResult;
import vn.com.be_crm.application.shared.dto.ImportRowError;
import vn.com.be_crm.domain.lead.entity.Lead;
import vn.com.be_crm.domain.lead.enums.LeadStatus;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Use case nhập hàng loạt Lead từ file Excel/CSV.
 * Mỗi dòng được xử lý độc lập; lỗi từng dòng được thu thập, không dừng toàn bộ.
 */
public class ImportBulkLeadUseCase {
    private final ILeadRepository repo;

    /** @param repo port lưu trữ */
    public ImportBulkLeadUseCase(ILeadRepository repo) { this.repo = repo; }

    /**
     * Xử lý nhập hàng loạt.
     * @param cmd dữ liệu import @return kết quả nhập
     */
    public ImportBulkResult execute(ImportBulkLeadCommand cmd) {
        List<ImportRowError> errors = new ArrayList<>();
        int success = 0;

        // Duyệt từng dòng; rowNum = i + 2 vì dòng 1 là header trong file Excel
        for (int i = 0; i < cmd.rows().size(); i++) {
            int rowNum = i + 2; // row 1 là header
            ImportLeadRowCommand row = cmd.rows().get(i);
            try {
                if (row.name() == null || row.name().isBlank()) {
                    // Gom lỗi theo từng dòng, không hủy cả lô — các dòng hợp lệ vẫn được lưu
                    errors.add(new ImportRowError(rowNum, "Trường 'Họ và tên' là bắt buộc"));
                    continue;
                }

                Long ownerId = resolveOwnerId(cmd, row);
                LeadStatus status = parseStatus(row.status());

                // Xác định nhánh thao tác theo importType: CREATE / UPDATE / BOTH
                boolean isUpdate = "UPDATE".equals(cmd.importType()) || "BOTH".equals(cmd.importType());
                boolean isCreate = "CREATE".equals(cmd.importType()) || "BOTH".equals(cmd.importType());

                // Tìm bản ghi trùng theo khóa duy nhất để cập nhật (chỉ khi cho phép UPDATE)
                Optional<Lead> existing = Optional.empty();
                if (isUpdate) {
                    if (row.phone() != null && !row.phone().isBlank())
                        existing = repo.findByPhone(row.phone());
                    if (existing.isEmpty() && row.email() != null && !row.email().isBlank())
                        existing = repo.findByEmail(row.email());
                }

                // Có bản ghi → cập nhật (giữ field cũ, ghi đè field có trong file)
                if (existing.isPresent()) {
                    Lead e = existing.get();
                    repo.save(Lead.builder()
                            .id(e.getId()).code(e.getCode())
                            .name(row.name()).ownerId(ownerId)
                            .customerId(e.getCustomerId()).contactId(e.getContactId())
                            .convertedOpportunityId(e.getConvertedOpportunityId())
                            .companyName(row.companyName() != null ? row.companyName() : e.getCompanyName())
                            .leadType(row.leadType() != null ? row.leadType() : e.getLeadType())
                            .title(row.title() != null ? row.title() : e.getTitle())
                            .department(row.department() != null ? row.department() : e.getDepartment())
                            .taxCode(row.taxCode() != null ? row.taxCode() : e.getTaxCode())
                            .website(row.website() != null ? row.website() : e.getWebsite())
                            .industry(row.industry() != null ? row.industry() : e.getIndustry())
                            .campaignId(row.campaignId() != null ? row.campaignId() : e.getCampaignId())
                            .source(row.source() != null ? row.source() : e.getSource())
                            .status(status != null ? status : e.getStatus())
                            .estimatedValue(row.estimatedValue() != null ? row.estimatedValue() : e.getEstimatedValue())
                            .phone(row.phone() != null ? row.phone() : e.getPhone())
                            .email(row.email() != null ? row.email() : e.getEmail())
                            .doNotCall(row.doNotCall() != null ? row.doNotCall() : e.isDoNotCall())
                            .doNotEmail(row.doNotEmail() != null ? row.doNotEmail() : e.isDoNotEmail())
                            .note(row.note() != null ? row.note() : e.getNote())
                            .createdAt(e.getCreatedAt()).build());
                    success++;
                // Chưa có và được phép tạo mới → thêm mới
                } else if (isCreate) {
                    String code = "IMP-" + System.currentTimeMillis() + "-" + rowNum;
                    repo.save(Lead.builder()
                            .code(code).name(row.name()).ownerId(ownerId)
                            .companyName(row.companyName()).leadType(row.leadType())
                            .title(row.title()).department(row.department()).taxCode(row.taxCode())
                            .website(row.website()).industry(row.industry()).campaignId(row.campaignId())
                            .source(row.source())
                            .status(status != null ? status : LeadStatus.new_)
                            .estimatedValue(row.estimatedValue())
                            .phone(row.phone()).email(row.email())
                            .doNotCall(row.doNotCall() != null && row.doNotCall())
                            .doNotEmail(row.doNotEmail() != null && row.doNotEmail())
                            .note(row.note())
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
