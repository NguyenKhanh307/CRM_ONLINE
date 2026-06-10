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
        for (int i = 0; i < cmd.rows().size(); i++) {
            int rowNum = i + 2;
            ImportCustomerRowCommand row = cmd.rows().get(i);
            try {
                if (row.name() == null || row.name().isBlank()) {
                    errors.add(new ImportRowError(rowNum, "Trường 'Tên khách hàng' là bắt buộc"));
                    continue;
                }
                Long ownerId = "SPECIFIC".equals(cmd.ownerMode()) ? cmd.specificOwnerId() : null;
                String code = "KH-" + System.currentTimeMillis() + "-" + rowNum;
                CustomerType type = parseEnum(CustomerType.class, row.type(), CustomerType.individual);
                CustomerStatus status = parseEnum(CustomerStatus.class, row.status(), CustomerStatus.active);
                repo.save(Customer.builder()
                        .code(code).name(row.name()).type(type)
                        .taxCode(row.taxCode()).phone(row.phone()).email(row.email())
                        .address(row.address()).source(row.source())
                        .status(status).ownerId(ownerId)
                        .build());
                success++;
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
}
