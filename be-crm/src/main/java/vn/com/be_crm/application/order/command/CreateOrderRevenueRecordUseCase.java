package vn.com.be_crm.application.order.command;

import vn.com.be_crm.application.order.dto.CreateOrderRevenueRecordCommand;
import vn.com.be_crm.application.order.dto.OrderRevenueRecordResult;
import vn.com.be_crm.application.order.mapper.OrderRevenueRecordCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.order.repository.IOrderRevenueRecordRepository;

/** Use case tạo mới bản ghi doanh thu đơn hàng. */
public class CreateOrderRevenueRecordUseCase implements IUseCase<CreateOrderRevenueRecordCommand, OrderRevenueRecordResult> {
    private final IOrderRevenueRecordRepository repo;
    /** @param repo port lưu trữ */
    public CreateOrderRevenueRecordUseCase(IOrderRevenueRecordRepository repo) { this.repo = repo; }
    /** Tạo mới OrderRevenueRecord. @param cmd @return OrderRevenueRecordResult */
    @Override public OrderRevenueRecordResult execute(CreateOrderRevenueRecordCommand cmd) {
        return OrderRevenueRecordCommandMapper.toResult(repo.save(OrderRevenueRecordCommandMapper.toEntity(cmd)));
    }
}
