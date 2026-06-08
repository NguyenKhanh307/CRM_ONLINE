package vn.com.be_crm.application.order.query;

import vn.com.be_crm.application.order.dto.OrderRevenueRecordResult;
import vn.com.be_crm.application.order.mapper.OrderRevenueRecordCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.order.repository.IOrderRevenueRecordRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy danh sách bản ghi doanh thu theo orderId. */
public class ListOrderRevenueRecordUseCase implements IUseCase<Long, List<OrderRevenueRecordResult>> {
    private final IOrderRevenueRecordRepository repo;
    /** @param repo port lưu trữ */
    public ListOrderRevenueRecordUseCase(IOrderRevenueRecordRepository repo) { this.repo = repo; }
    /** Lấy danh sách OrderRevenueRecord theo orderId. @param orderId @return danh sách */
    @Override public List<OrderRevenueRecordResult> execute(Long orderId) {
        return repo.findAllByOrderId(orderId).stream().map(OrderRevenueRecordCommandMapper::toResult).collect(Collectors.toList());
    }
}
