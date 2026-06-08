package vn.com.be_crm.application.customer.query;

import vn.com.be_crm.application.customer.dto.InventoryCheckResult;
import vn.com.be_crm.application.customer.mapper.InventoryCheckCommandMapper;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.customer.repository.IInventoryCheckRepository;

import java.util.stream.Collectors;

/** Use case lấy danh sách phiếu kiểm kho có phân trang. */
public class ListInventoryCheckUseCase implements IUseCase<PageRequest, PageResult<InventoryCheckResult>> {
    private final IInventoryCheckRepository repo;
    /** @param repo port lưu trữ */
    public ListInventoryCheckUseCase(IInventoryCheckRepository repo) { this.repo = repo; }

    /**
     * Lấy danh sách InventoryCheck có phân trang.
     * @param r tham số phân trang @return PageResult
     */
    @Override
    public PageResult<InventoryCheckResult> execute(PageRequest r) {
        var page = repo.findAll(r);
        return PageResult.<InventoryCheckResult>builder()
                .items(page.getItems().stream().map(InventoryCheckCommandMapper::toResult).collect(Collectors.toList()))
                .total(page.getTotal()).page(page.getPage()).size(page.getSize()).build();
    }
}
