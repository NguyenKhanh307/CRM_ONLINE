package vn.com.be_crm.application.order.command;

import vn.com.be_crm.application.order.dto.OrderRevenueRecordResult;
import vn.com.be_crm.application.order.dto.UpdateOrderRevenueRecordCommand;
import vn.com.be_crm.application.order.mapper.OrderRevenueRecordCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.order.entity.OrderRevenueRecord;
import vn.com.be_crm.domain.order.repository.IOrderRevenueRecordRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case cập nhật bản ghi doanh thu đơn hàng. */
public class UpdateOrderRevenueRecordUseCase implements IUseCase<UpdateOrderRevenueRecordCommand, OrderRevenueRecordResult> {
    private final IOrderRevenueRecordRepository repo;
    /** @param repo port lưu trữ */
    public UpdateOrderRevenueRecordUseCase(IOrderRevenueRecordRepository repo) { this.repo = repo; }
    /** Cập nhật OrderRevenueRecord. @param cmd @return OrderRevenueRecordResult @throws NotFoundException */
    @Override public OrderRevenueRecordResult execute(UpdateOrderRevenueRecordCommand cmd) {
        OrderRevenueRecord e = repo.findById(cmd.getId()).orElseThrow(() -> new NotFoundException("OrderRevenueRecord not found: " + cmd.getId()));
        return OrderRevenueRecordCommandMapper.toResult(repo.save(OrderRevenueRecordCommandMapper.toEntity(cmd, e)));
    }
}
