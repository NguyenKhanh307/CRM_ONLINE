package vn.com.be_crm.application.order.query;

import vn.com.be_crm.application.order.dto.OrderResult;
import vn.com.be_crm.application.order.mapper.OrderCommandMapper;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.order.repository.IOrderRepository;

import java.util.stream.Collectors;

/** Use case lấy danh sách đơn hàng có phân trang. */
public class ListOrderUseCase implements IUseCase<PageRequest, PageResult<OrderResult>> {
    private final IOrderRepository repo;
    /** @param repo port lưu trữ */
    public ListOrderUseCase(IOrderRepository repo) { this.repo = repo; }
    /** Lấy danh sách Order. @param r phân trang @return PageResult */
    @Override public PageResult<OrderResult> execute(PageRequest r) {
        var page = repo.findAll(r);
        return PageResult.<OrderResult>builder()
                .items(page.getItems().stream().map(OrderCommandMapper::toResult).collect(Collectors.toList()))
                .total(page.getTotal()).page(page.getPage()).size(page.getSize()).build();
    }
}
