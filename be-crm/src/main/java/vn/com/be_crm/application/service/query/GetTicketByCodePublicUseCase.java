package vn.com.be_crm.application.service.query;

import vn.com.be_crm.application.service.dto.PublicTicketView;
import vn.com.be_crm.domain.service.entity.Ticket;
import vn.com.be_crm.domain.service.repository.ITicketRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/**
 * Use case lấy phiếu chăm sóc theo mã để hiển thị trên trang public
 * (`/support-page/{code}`) — khách xem trạng thái + tự đánh giá, không cần đăng nhập.
 */
public class GetTicketByCodePublicUseCase {
    private final ITicketRepository repo;

    /** @param repo port lưu trữ */
    public GetTicketByCodePublicUseCase(ITicketRepository repo) { this.repo = repo; }

    /**
     * Lấy thông tin công khai của phiếu theo mã.
     * @param code mã phiếu @return PublicTicketView
     */
    public PublicTicketView execute(String code) {
        Ticket t = repo.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy phiếu với mã: " + code));
        return toView(t);
    }

    /** Chuyển domain entity sang view công khai. @param t @return PublicTicketView */
    public static PublicTicketView toView(Ticket t) {
        return new PublicTicketView(
                t.getCode(),
                t.getType() != null ? t.getType().name() : null,
                t.getStatus() != null ? t.getStatus().name() : null,
                t.getPriority() != null ? t.getPriority().name() : null,
                t.getSubject(),
                t.getDescription(),
                t.getCreatedAt(),
                t.getSlaDueAt(),
                t.getResolvedAt(),
                t.getClosedAt(),
                t.getSatisfactionScore(),
                t.getSatisfactionComment());
    }
}
