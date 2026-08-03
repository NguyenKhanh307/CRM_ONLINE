package vn.com.be_crm.application.quotation.query;

import vn.com.be_crm.application.quotation.dto.QuotationEmailDraft;
import vn.com.be_crm.application.quotation.email.QuotationEmailComposer;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/**
 * Use case xem-trước nội dung email báo giá mặc định (để FE hiển thị trong ô soạn trước khi gửi).
 * Không đổi trạng thái báo giá. Nếu chưa có email người nhận vẫn trả draft (toEmail rỗng) để FE cảnh báo nhẹ.
 */
public class GetQuotationEmailDraftUseCase implements IUseCase<Long, QuotationEmailDraft> {
    private final IQuotationRepository repo;
    private final QuotationEmailComposer composer;

    /** @param repo port lưu trữ báo giá @param composer bộ dựng nội dung email mặc định */
    public GetQuotationEmailDraftUseCase(IQuotationRepository repo, QuotationEmailComposer composer) {
        this.repo = repo;
        this.composer = composer;
    }

    /** Lấy nội dung email mặc định theo ID báo giá. @param id ID @return draft @throws NotFoundException */
    @Override public QuotationEmailDraft execute(Long id) {
        Quotation q = repo.findById(id).orElseThrow(() -> new NotFoundException("Quotation not found: " + id));
        return composer.draft(q);
    }
}
