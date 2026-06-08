package vn.com.be_crm.application.customer.command;

import vn.com.be_crm.application.customer.dto.InventoryCheckResult;
import vn.com.be_crm.application.customer.dto.UpdateInventoryCheckCommand;
import vn.com.be_crm.application.customer.mapper.InventoryCheckCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.customer.entity.InventoryCheck;
import vn.com.be_crm.domain.customer.repository.IInventoryCheckRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case cập nhật phiếu kiểm kho. */
public class UpdateInventoryCheckUseCase implements IUseCase<UpdateInventoryCheckCommand, InventoryCheckResult> {
    private final IInventoryCheckRepository repo;
    /** @param repo port lưu trữ */
    public UpdateInventoryCheckUseCase(IInventoryCheckRepository repo) { this.repo = repo; }

    /**
     * Cập nhật InventoryCheck và trả về result.
     * @param cmd dữ liệu cập nhật @return InventoryCheckResult
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public InventoryCheckResult execute(UpdateInventoryCheckCommand cmd) {
        InventoryCheck existing = repo.findById(cmd.getId())
                .orElseThrow(() -> new NotFoundException("InventoryCheck not found: " + cmd.getId()));
        return InventoryCheckCommandMapper.toResult(repo.save(InventoryCheckCommandMapper.toEntity(cmd, existing)));
    }
}
