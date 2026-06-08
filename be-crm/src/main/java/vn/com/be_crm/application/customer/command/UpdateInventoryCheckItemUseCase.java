package vn.com.be_crm.application.customer.command;

import vn.com.be_crm.application.customer.dto.InventoryCheckItemResult;
import vn.com.be_crm.application.customer.dto.UpdateInventoryCheckItemCommand;
import vn.com.be_crm.application.customer.mapper.InventoryCheckItemCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.customer.entity.InventoryCheckItem;
import vn.com.be_crm.domain.customer.repository.IInventoryCheckItemRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case cập nhật dòng kiểm kho. */
public class UpdateInventoryCheckItemUseCase implements IUseCase<UpdateInventoryCheckItemCommand, InventoryCheckItemResult> {
    private final IInventoryCheckItemRepository repo;
    /** @param repo port lưu trữ */
    public UpdateInventoryCheckItemUseCase(IInventoryCheckItemRepository repo) { this.repo = repo; }

    /**
     * Cập nhật InventoryCheckItem và trả về result.
     * @param cmd dữ liệu cập nhật @return InventoryCheckItemResult
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public InventoryCheckItemResult execute(UpdateInventoryCheckItemCommand cmd) {
        InventoryCheckItem existing = repo.findById(cmd.getId())
                .orElseThrow(() -> new NotFoundException("InventoryCheckItem not found: " + cmd.getId()));
        return InventoryCheckItemCommandMapper.toResult(repo.save(InventoryCheckItemCommandMapper.toEntity(cmd, existing)));
    }
}
