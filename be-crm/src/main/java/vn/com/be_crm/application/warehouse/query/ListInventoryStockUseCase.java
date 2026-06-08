package vn.com.be_crm.application.warehouse.query;

import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.application.warehouse.dto.InventoryStockResult;
import vn.com.be_crm.application.warehouse.mapper.WarehouseCommandMapper;
import vn.com.be_crm.domain.warehouse.repository.IInventoryStockRepository;

import java.util.stream.Collectors;

/** Use case lấy danh sách tồn kho có phân trang. */
public class ListInventoryStockUseCase implements IUseCase<PageRequest, PageResult<InventoryStockResult>> {
    private final IInventoryStockRepository repo;
    /** @param repo port lưu trữ */
    public ListInventoryStockUseCase(IInventoryStockRepository repo) { this.repo = repo; }
    /** @param r request @return PageResult */
    @Override public PageResult<InventoryStockResult> execute(PageRequest r) {
        var p = repo.findAll(r);
        return PageResult.<InventoryStockResult>builder()
                .items(p.getItems().stream().map(WarehouseCommandMapper::toStockResult).collect(Collectors.toList()))
                .total(p.getTotal()).page(p.getPage()).size(p.getSize()).build();
    }
}
