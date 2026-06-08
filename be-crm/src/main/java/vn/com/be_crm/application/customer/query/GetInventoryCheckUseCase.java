package vn.com.be_crm.application.customer.query;

import vn.com.be_crm.application.customer.dto.InventoryCheckResult;
import vn.com.be_crm.application.customer.mapper.InventoryCheckCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.customer.repository.IInventoryCheckRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case lấy phiếu kiểm kho theo ID. */
public class GetInventoryCheckUseCase implements IUseCase<Long, InventoryCheckResult> {
    private final IInventoryCheckRepository repo;
    /** @param repo port lưu trữ */
    public GetInventoryCheckUseCase(IInventoryCheckRepository repo) { this.repo = repo; }

    /**
     * Lấy InventoryCheck theo ID.
     * @param id ID @return InventoryCheckResult
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public InventoryCheckResult execute(Long id) {
        return InventoryCheckCommandMapper.toResult(
                repo.findById(id).orElseThrow(() -> new NotFoundException("InventoryCheck not found: " + id)));
    }
}
