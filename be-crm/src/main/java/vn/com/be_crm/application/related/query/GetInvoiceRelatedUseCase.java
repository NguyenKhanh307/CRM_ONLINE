package vn.com.be_crm.application.related.query;

import vn.com.be_crm.application.related.dto.InvoiceRelatedResult;
import vn.com.be_crm.domain.invoice.entity.Invoice;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRepository;
import vn.com.be_crm.domain.related.repository.IRelatedRepository;
import vn.com.be_crm.core.error.frontend.ForbiddenException;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/**
 * Lấy bản ghi liên quan của một hóa đơn cho trang chi tiết 360°.
 * Quyền kiểm tra MỘT LẦN trên hóa đơn (bản ghi cha); qua được thì trả đủ bản ghi con.
 */
public class GetInvoiceRelatedUseCase {

    private final IInvoiceRepository invoiceRepo;
    private final IRelatedRepository relatedRepo;

    /** @param invoiceRepo port hóa đơn @param relatedRepo port bản ghi liên quan */
    public GetInvoiceRelatedUseCase(IInvoiceRepository invoiceRepo, IRelatedRepository relatedRepo) {
        this.invoiceRepo = invoiceRepo;
        this.relatedRepo = relatedRepo;
    }

    /**
     * @param invoiceId  ID hóa đơn
     * @param userId     ID người đang đăng nhập
     * @param privileged true nếu ADMIN/SALES_MANAGER (xem mọi hóa đơn)
     * @return các nhóm bản ghi liên quan
     */
    public InvoiceRelatedResult execute(Long invoiceId, Long userId, boolean privileged) {
        Invoice i = invoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new NotFoundException("Invoice", invoiceId));
        if (!privileged && (i.getOwnerId() == null || !i.getOwnerId().equals(userId))) {
            throw new ForbiddenException("Bạn không có quyền xem hóa đơn này");
        }
        return relatedRepo.getInvoiceRelated(invoiceId);
    }
}
