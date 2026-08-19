package vn.com.be_crm.application.order.command;

import vn.com.be_crm.application.order.dto.OrderResult;
import vn.com.be_crm.application.order.mapper.OrderCommandMapper;
import vn.com.be_crm.domain.order.entity.Order;
import vn.com.be_crm.domain.order.enums.OrderStatus;
import vn.com.be_crm.domain.order.repository.IOrderRepository;
import vn.com.be_crm.core.error.frontend.DomainException;
import vn.com.be_crm.core.error.frontend.NotFoundException;
import vn.com.be_crm.core.tx.port.ITransactionRunner;

// đánh dấu đơn hàng đã được nhân viên tự tay xuất hóa đơn (FE đã tạo Hóa đơn riêng qua AddPage,
// dòng hàng do FE tự điền — xem InvoiceAddPage?fromOrder=). Use case này CHỈ còn khóa đơn hàng
// (audit trail) + chuyển sang Hoàn tất (completed), không tạo bản ghi nào.
public class MarkOrderConvertedUseCase {
    private final IOrderRepository orderRepo;
    private final ITransactionRunner tx;

    public MarkOrderConvertedUseCase(IOrderRepository orderRepo, ITransactionRunner tx) {
        this.orderRepo = orderRepo;
        this.tx = tx;
    }

    // khóa đơn hàng + hoàn tất — chạy trong MỘT transaction
    public OrderResult execute(Long orderId) {
        return tx.call(() -> executeInTx(orderId));
    }

    private OrderResult executeInTx(Long orderId) {
        Order o = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));
        if (o.isLocked()) throw new DomainException("Đơn hàng đã xuất hóa đơn trước đó");

        Order locked = o.toBuilder().isLocked(true)
                .status(o.getStatus() != OrderStatus.completed ? OrderStatus.completed : o.getStatus())
                .build();
        return OrderCommandMapper.toResult(orderRepo.save(locked));
    }
}
