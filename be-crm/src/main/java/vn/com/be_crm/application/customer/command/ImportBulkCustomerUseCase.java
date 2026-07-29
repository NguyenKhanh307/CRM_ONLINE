package vn.com.be_crm.application.customer.command;

import vn.com.be_crm.application.customer.dto.ImportBulkCustomerCommand;
import vn.com.be_crm.application.customer.dto.ImportCustomerRowCommand;
import vn.com.be_crm.application.shared.dto.ImportBulkResult;
import vn.com.be_crm.application.shared.dto.ImportRowError;
import vn.com.be_crm.domain.customer.entity.Customer;
import vn.com.be_crm.domain.customer.enums.CustomerStatus;
import vn.com.be_crm.domain.customer.enums.CustomerType;
import vn.com.be_crm.domain.customer.repository.ICustomerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Use case nhập hàng loạt Customer từ file Excel/CSV. */
public class ImportBulkCustomerUseCase {
    private final ICustomerRepository repo;

    /** @param repo port lưu trữ */
    public ImportBulkCustomerUseCase(ICustomerRepository repo) { this.repo = repo; }

    /**
     * Xử lý nhập hàng loạt Customer.
     * @param cmd dữ liệu import @return kết quả nhập
     */
    public ImportBulkResult execute(ImportBulkCustomerCommand cmd) {
        List<ImportRowError> errors = new ArrayList<>();
        int success = 0;
        // Duyệt từng dòng; rowNum = i + 2 vì dòng 1 là header trong file Excel
        for (int i = 0; i < cmd.rows().size(); i++) {
            int rowNum = i + 2;
            ImportCustomerRowCommand row = cmd.rows().get(i);
            try {
                if (row.name() == null || row.name().isBlank()) {
                    // Gom lỗi theo từng dòng, không hủy cả lô — các dòng hợp lệ vẫn được lưu
                    errors.add(new ImportRowError(rowNum, "Trường 'Tên khách hàng' là bắt buộc"));
                    continue;
                }
                // Xác định owner: gán cố định theo cấu hình hoặc null (lấy từ dòng/bản ghi cũ)
                Long ownerId = "SPECIFIC".equals(cmd.ownerMode()) ? cmd.specificOwnerId() : null;
                CustomerType type = parseEnum(CustomerType.class, row.type(), CustomerType.individual);
                CustomerStatus status = parseEnum(CustomerStatus.class, row.status(), CustomerStatus.active);

                // Xác định nhánh thao tác theo importType: CREATE / UPDATE / BOTH
                boolean isUpdate = "UPDATE".equals(cmd.importType()) || "BOTH".equals(cmd.importType());
                boolean isCreate = "CREATE".equals(cmd.importType()) || "BOTH".equals(cmd.importType());

                // Tìm bản ghi trùng theo khóa duy nhất để cập nhật (chỉ khi cho phép UPDATE)
                Optional<Customer> existing = Optional.empty();
                if (isUpdate && row.taxCode() != null && !row.taxCode().isBlank())
                    existing = repo.findByTaxCode(row.taxCode());

                // Có bản ghi → cập nhật (giữ field cũ, ghi đè field có trong file)
                if (existing.isPresent()) {
                    Customer e = existing.get();
                    repo.save(Customer.builder()
                            .id(e.getId()).code(e.getCode()).name(row.name())
                            .shortName(row.shortName() != null ? row.shortName() : e.getShortName())
                            .type(type)
                            .taxCode(row.taxCode() != null ? row.taxCode() : e.getTaxCode())
                            .phone(row.phone() != null ? row.phone() : e.getPhone())
                            .email(row.email() != null ? row.email() : e.getEmail())
                            .website(row.website() != null ? row.website() : e.getWebsite())
                            .address(row.address() != null ? row.address() : e.getAddress())
                            .industry(row.industry() != null ? row.industry() : e.getIndustry())
                            .source(row.source() != null ? row.source() : e.getSource())
                            .status(status)
                            .creditDays(row.creditDays() != null ? row.creditDays() : e.getCreditDays())
                            .creditLimit(row.creditLimit() != null ? row.creditLimit() : e.getCreditLimit())
                            .bankAccount(row.bankAccount() != null ? row.bankAccount() : e.getBankAccount())
                            .bankName(row.bankName() != null ? row.bankName() : e.getBankName())
                            .rating(row.rating() != null ? row.rating() : e.getRating())
                            .annualRevenue(row.annualRevenue() != null ? row.annualRevenue() : e.getAnnualRevenue())
                            .employeeSize(row.employeeSize() != null ? row.employeeSize() : e.getEmployeeSize())
                            .isDistributor(row.isDistributor() != null ? row.isDistributor() : e.isDistributor())
                            .ownerId(ownerId != null ? ownerId : e.getOwnerId())
                            .createdAt(e.getCreatedAt()).build());
                    success++;
                // Chưa có và được phép tạo mới → thêm mới
                } else if (isCreate) {
                    String code = "KH-" + System.currentTimeMillis() + "-" + rowNum;
                    repo.save(Customer.builder()
                            .code(code).name(row.name()).shortName(row.shortName()).type(type)
                            .taxCode(row.taxCode()).phone(row.phone()).email(row.email())
                            .website(row.website()).address(row.address()).industry(row.industry())
                            .source(row.source())
                            .status(status).ownerId(ownerId)
                            .creditDays(row.creditDays()).creditLimit(row.creditLimit())
                            .bankAccount(row.bankAccount()).bankName(row.bankName())
                            .rating(row.rating()).annualRevenue(row.annualRevenue())
                            .employeeSize(row.employeeSize())
                            .isDistributor(row.isDistributor() != null && row.isDistributor())
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

    private <E extends Enum<E>> E parseEnum(Class<E> cls, String val, E def) {
        if (val == null || val.isBlank()) return def;
        try { return Enum.valueOf(cls, val.trim().toLowerCase()); }
        catch (Exception e) { return def; }
    }
}
