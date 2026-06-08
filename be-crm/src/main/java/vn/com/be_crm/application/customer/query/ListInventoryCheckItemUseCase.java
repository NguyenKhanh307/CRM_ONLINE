package vn.com.be_crm.application.customer.query;

import vn.com.be_crm.application.customer.dto.InventoryCheckItemResult;
import vn.com.be_crm.application.customer.mapper.InventoryCheckItemCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.customer.repository.IInventoryCheckItemRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy danh sách dòng kiểm kho theo checkId. */
public class ListInventoryCheckItemUseCase implements IUseCase<Long, List<InventoryCheckItemResult>> {
    private final IInventoryCheckItemRepository repo;
    /** @param repo port lưu trữ */
    public ListInventoryCheckItemUseCase(IInventoryCheckItemRepository repo) { this.repo = repo; }

    /**
     * Lấy danh sách InventoryCheckItem theo checkId.
     * @param checkId ID phiếu kiểm kho @return danh sách result
     */
    @Override
    public List<InventoryCheckItemResult> execute(Long checkId) {
        return repo.findAllByCheckId(checkId).stream()
                .map(InventoryCheckItemCommandMapper::toResult).collect(Collectors.toList());
    }
}
