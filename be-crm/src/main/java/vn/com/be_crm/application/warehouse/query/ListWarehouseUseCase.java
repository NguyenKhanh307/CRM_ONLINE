package vn.com.be_crm.application.warehouse.query;

import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.application.warehouse.dto.WarehouseResult;
import vn.com.be_crm.application.warehouse.mapper.WarehouseCommandMapper;
import vn.com.be_crm.domain.warehouse.repository.IWarehouseRepository;

import java.util.stream.Collectors;

/** Use case lấy danh sách kho hàng có phân trang. */
public class ListWarehouseUseCase implements IUseCase<PageRequest, PageResult<WarehouseResult>> {
    private final IWarehouseRepository repo;
    /** @param repo port lưu trữ */
    public ListWarehouseUseCase(IWarehouseRepository repo) { this.repo = repo; }
    /** @param r request @return PageResult */
    @Override public PageResult<WarehouseResult> execute(PageRequest r) {
        var p = repo.findAll(r);
        return PageResult.<WarehouseResult>builder()
                .items(p.getItems().stream().map(WarehouseCommandMapper::toResult).collect(Collectors.toList()))
                .total(p.getTotal()).page(p.getPage()).size(p.getSize()).build();
    }
}
