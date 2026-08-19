package vn.com.be_crm.application.pricing.command;

import vn.com.be_crm.application.pricing.dto.ImportBulkPricePolicyCommand;
import vn.com.be_crm.application.pricing.dto.ImportPricePolicyRowCommand;
import vn.com.be_crm.core.dto.import_.ImportBulkResult;
import vn.com.be_crm.core.dto.import_.ImportRowError;
import vn.com.be_crm.core.tx.port.ITransactionRunner;
import vn.com.be_crm.domain.pricing.entity.PricePolicy;
import vn.com.be_crm.domain.pricing.enums.PricePolicyStatus;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyRepository;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

// nhập hàng loạt PricePolicy (chính sách giá) từ file Excel/CSV — chỉ bảng header. Validate
// TOÀN BỘ trước (pass 1, không đụng DB), chỉ khi sạch hết mới lưu TOÀN BỘ trong 1 transaction
// (pass 2)
public class ImportBulkPricePolicyUseCase {
    private final IPricePolicyRepository repo;
    private final ITransactionRunner txRunner;

    /** @param repo port lưu trữ @param txRunner chạy pass 2 trong 1 transaction */
    public ImportBulkPricePolicyUseCase(IPricePolicyRepository repo, ITransactionRunner txRunner) {
        this.repo = repo;
        this.txRunner = txRunner;
    }

    /**
     * Xử lý nhập hàng loạt PricePolicy.
     * @param cmd dữ liệu import @return kết quả nhập
     */
    public ImportBulkResult execute(ImportBulkPricePolicyCommand cmd) {
        List<ImportRowError> errors = new ArrayList<>();
        Set<String> seenCodes = new HashSet<>();

        // ----- Pass 1: validate mọi dòng, không chạm DB (trừ check trùng mã đọc-thôi bên dưới) -----
        for (int i = 0; i < cmd.rows().size(); i++) {
            int rowNum = i + 2;
            ImportPricePolicyRowCommand row = cmd.rows().get(i);
            if (row.name() == null || row.name().isBlank()) {
                errors.add(new ImportRowError(rowNum, "Trường 'Tên chính sách' là bắt buộc"));
                continue;
            }
            String err = null;
            try { parseType(row.type()); } catch (Exception e) { err = e.getMessage(); }
            if (err == null && row.status() != null && !row.status().isBlank() && parseStatus(row.status()) == null) {
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
                    ImportPricePolicyRowCommand row = cmd.rows().get(i);
                    PricePolicyStatus status = parseStatus(row.status());
                    String type = parseType(row.type());
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
                                .type(type != null ? type : e.getType())
                                .priority(row.priority() != null ? row.priority() : e.getPriority())
                                .startDate(startDate != null ? startDate : e.getStartDate())
                                .endDate(endDate != null ? endDate : e.getEndDate())
                                .status(status != null ? status : e.getStatus())
                                .createdBy(e.getCreatedBy()).createdAt(e.getCreatedAt())
                                .build());
                        success[0]++;
                    } else if (isCreate) {
                        String code = (row.code() != null && !row.code().isBlank())
                                ? row.code() : "CSG-" + System.currentTimeMillis() + "-" + rowNum;
                        repo.save(PricePolicy.builder()
                                .code(code).name(row.name()).type(type)
                                .priority(row.priority() != null ? row.priority() : 0)
                                .startDate(startDate).endDate(endDate)
                                .status(status != null ? status : PricePolicyStatus.active)
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

    private PricePolicyStatus parseStatus(String s) {
        if (s == null || s.isBlank()) return null;
        try { return PricePolicyStatus.valueOf(s.trim().toLowerCase()); }
        catch (Exception e) { return null; }
    }

    private static final java.util.Set<String> VALID_TYPES = java.util.Set.of("standard", "promotional", "special");

    // khác parseStatus() ở chỗ giá trị sai KHÔNG âm thầm trả null — type là cột ENUM thật trong DB,
    // để lọt chuỗi tự do sẽ vỡ ở tầng insert với thông báo lỗi SQL khó hiểu; báo lỗi rõ ràng ngay tại đây
    private String parseType(String s) {
        if (s == null || s.isBlank()) return null;
        String v = s.trim().toLowerCase();
        if (!VALID_TYPES.contains(v)) {
            throw new IllegalArgumentException("Loại chính sách không hợp lệ: \"" + s + "\" (chỉ nhận standard/promotional/special)");
        }
        return v;
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try { return LocalDate.parse(s.trim()); }
        catch (DateTimeParseException e) { return null; }
    }
}
