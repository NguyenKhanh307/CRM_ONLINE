package vn.com.be_crm.application.service.command;

import vn.com.be_crm.application.service.dto.PublicTicketView;
import vn.com.be_crm.application.service.query.GetTicketByCodePublicUseCase;
import vn.com.be_crm.domain.service.entity.Ticket;
import vn.com.be_crm.domain.service.repository.ITicketRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/**
 * Use case khách tự đánh giá hài lòng (CSAT) qua trang public theo mã phiếu
 * (`/support-page/{code}`) — bọc {@link SubmitCsatUseCase} (đã có sẵn guard resolved/closed + điểm 1-5),
 * chỉ thêm bước tra mã phiếu → ID vì trang public không biết ID nội bộ.
 */
public class SubmitCsatByCodeUseCase {
    private final ITicketRepository repo;
    private final SubmitCsatUseCase submitCsatUseCase;

    /** @param repo port lưu trữ @param submitCsatUseCase use case ghi điểm CSAT sẵn có */
    public SubmitCsatByCodeUseCase(ITicketRepository repo, SubmitCsatUseCase submitCsatUseCase) {
        this.repo = repo;
        this.submitCsatUseCase = submitCsatUseCase;
    }

    /**
     * Ghi điểm hài lòng cho phiếu theo mã.
     * @param code mã phiếu @param score điểm 1-5 @param comment nhận xét @return PublicTicketView sau cập nhật
     */
    public PublicTicketView execute(String code, Integer score, String comment) {
        Ticket t = repo.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy phiếu với mã: " + code));
        submitCsatUseCase.execute(t.getId(), score, comment);
        Ticket updated = repo.findById(t.getId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy phiếu với mã: " + code));
        return GetTicketByCodePublicUseCase.toView(updated);
    }
}
