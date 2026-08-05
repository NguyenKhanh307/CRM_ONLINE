package vn.com.be_crm.application.order.command;

import vn.com.be_crm.application.lead.command.NotifyLeadFirstOrderUseCase;
import vn.com.be_crm.core.util.CrossFieldRules;
import vn.com.be_crm.application.order.dto.CreateOrderCommand;
import vn.com.be_crm.application.order.dto.OrderResult;
import vn.com.be_crm.application.order.mapper.OrderCommandMapper;
import vn.com.be_crm.application.order.mapper.OrderItemCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.core.error.frontend.DomainException;
import vn.com.be_crm.core.util.LineItemTotals;
import vn.com.be_crm.domain.order.entity.Order;
import vn.com.be_crm.domain.order.entity.OrderItem;
import vn.com.be_crm.domain.order.repository.IOrderRepository;

import java.util.List;
import java.util.stream.Collectors;

// tạo mới đơn hàng (kèm dòng hàng nếu có). Thành tiền từng dòng và tổng tiền chứng từ do SERVER
// tính tại thời điểm đọc — bỏ qua số tiền client gửi lên, không lưu vào cột nào.
public class CreateOrderUseCase implements IUseCase<CreateOrderCommand, OrderResult> {
    private final IOrderRepository repo;
    private final NotifyLeadFirstOrderUseCase notifyLeadFirstOrderUC;

    public CreateOrderUseCase(IOrderRepository repo, NotifyLeadFirstOrderUseCase notifyLeadFirstOrderUC) {
        this.repo = repo;
        this.notifyLeadFirstOrderUC = notifyLeadFirstOrderUC;
    }

    @Override public OrderResult execute(CreateOrderCommand cmd) {
        // ràng buộc khoảng thời gian: ngày giao hàng không được trước ngày đặt hàng
        CrossFieldRules.requireDateRange(cmd.getOrderDate(), cmd.getDeliveryDate(), "Ngày đặt hàng", "Ngày giao hàng");
        // check trùng thân thiện trước khi insert (DB unique constraint là lớp phòng thủ 2)
        if (cmd.getCode() != null && repo.findByCode(cmd.getCode()).isPresent()) {
            throw new DomainException("Mã đơn hàng \"" + cmd.getCode() + "\" đã tồn tại, vui lòng dùng mã khác");
        }
        var entity = OrderCommandMapper.toEntity(cmd);
        List<OrderItem> items = cmd.getItems() != null
                ? cmd.getItems().stream().map(OrderItemCommandMapper::toEntity).collect(Collectors.toList())
                : List.of();
        Order saved = items.isEmpty() ? repo.save(entity) : repo.saveWithItems(entity, items);
        // đơn tạo tay có gắn báo giá -> kiểm xem có phải đơn đầu tiên của tiềm năng nguồn không
        notifyLeadFirstOrderUC.execute(saved.getQuotationId(), saved.getId());

        OrderResult result = OrderCommandMapper.toResult(saved);
        LineItemTotals.Totals t = LineItemTotals.compute(items,
                OrderItem::getQuantity, OrderItem::getUnitPrice, OrderItem::getDiscount, OrderItem::getTaxRate);
        result.setSubtotal(t.subtotal()); result.setDiscount(t.discount());
        result.setTax(t.tax()); result.setTotal(t.total());
        return result;
    }
}
