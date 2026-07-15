package vn.com.be_crm.application.related.query;

import vn.com.be_crm.application.related.dto.OrderRelatedResult;
import vn.com.be_crm.domain.order.entity.Order;
import vn.com.be_crm.domain.order.repository.IOrderRepository;
import vn.com.be_crm.domain.related.repository.IRelatedRepository;
import vn.com.be_crm.domain.shared.exception.ForbiddenException;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/**
 * Lấy bản ghi liên quan của một đơn hàng cho trang chi tiết 360°.
 * Quyền kiểm tra MỘT LẦN trên đơn hàng (bản ghi cha); qua được thì trả đủ bản ghi con.
 */
public class GetOrderRelatedUseCase {

    private final IOrderRepository orderRepo;
    private final IRelatedRepository relatedRepo;

    /** @param orderRepo port đơn hàng @param relatedRepo port bản ghi liên quan */
    public GetOrderRelatedUseCase(IOrderRepository orderRepo, IRelatedRepository relatedRepo) {
        this.orderRepo = orderRepo;
        this.relatedRepo = relatedRepo;
    }

    /**
     * @param orderId    ID đơn hàng
     * @param userId     ID người đang đăng nhập
     * @param privileged true nếu ADMIN/SALES_MANAGER (xem mọi đơn hàng)
     * @return các nhóm bản ghi liên quan
     */
    public OrderRelatedResult execute(Long orderId, Long userId, boolean privileged) {
        Order o = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order", orderId));
        if (!privileged && (o.getOwnerId() == null || !o.getOwnerId().equals(userId))) {
            throw new ForbiddenException("Bạn không có quyền xem đơn hàng này");
        }
        return relatedRepo.getOrderRelated(orderId);
    }
}
