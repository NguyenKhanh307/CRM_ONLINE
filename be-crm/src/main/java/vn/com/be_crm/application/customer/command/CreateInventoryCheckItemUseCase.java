package vn.com.be_crm.application.customer.command;

import vn.com.be_crm.application.customer.dto.CreateInventoryCheckItemCommand;
import vn.com.be_crm.application.customer.dto.InventoryCheckItemResult;
import vn.com.be_crm.application.customer.mapper.InventoryCheckItemCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.customer.repository.IInventoryCheckItemRepository;

/** Use case tạo mới dòng kiểm kho. */
public class CreateInventoryCheckItemUseCase implements IUseCase<CreateInventoryCheckItemCommand, InventoryCheckItemResult> {
    private final IInventoryCheckItemRepository repo;
    /** @param repo port lưu trữ */
    public CreateInventoryCheckItemUseCase(IInventoryCheckItemRepository repo) { this.repo = repo; }

    /**
     * Tạo mới InventoryCheckItem và trả về result.
     * @param cmd dữ liệu tạo mới @return InventoryCheckItemResult
     */
    @Override
    public InventoryCheckItemResult execute(CreateInventoryCheckItemCommand cmd) {
        return InventoryCheckItemCommandMapper.toResult(repo.save(InventoryCheckItemCommandMapper.toEntity(cmd)));
    }
}
