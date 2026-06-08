package vn.com.be_crm.application.customer.command;

import vn.com.be_crm.application.customer.dto.CreateInventoryCheckCommand;
import vn.com.be_crm.application.customer.dto.InventoryCheckResult;
import vn.com.be_crm.application.customer.mapper.InventoryCheckCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.customer.repository.IInventoryCheckRepository;

/** Use case tạo mới phiếu kiểm kho. */
public class CreateInventoryCheckUseCase implements IUseCase<CreateInventoryCheckCommand, InventoryCheckResult> {
    private final IInventoryCheckRepository repo;
    /** @param repo port lưu trữ */
    public CreateInventoryCheckUseCase(IInventoryCheckRepository repo) { this.repo = repo; }

    /**
     * Tạo mới InventoryCheck và trả về result.
     * @param cmd dữ liệu tạo mới @return InventoryCheckResult
     */
    @Override
    public InventoryCheckResult execute(CreateInventoryCheckCommand cmd) {
        return InventoryCheckCommandMapper.toResult(repo.save(InventoryCheckCommandMapper.toEntity(cmd)));
    }
}
