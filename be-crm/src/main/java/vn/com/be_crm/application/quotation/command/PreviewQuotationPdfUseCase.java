package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.application.quotation.email.QuotationEmailComposer;
import vn.com.be_crm.application.quotation.pdf.QuotationPdfDataBuilder;
import vn.com.be_crm.application.shared.pdf.IQuotationPdfService;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/**
 * Use case xem trước PDF báo giá — dùng khi soạn email gửi khách (trước khi bấm gửi) để kiểm tra
 * bố cục/nội dung trước. KHÔNG đổi trạng thái báo giá, không gửi email, không sinh token mới.
 */
public class PreviewQuotationPdfUseCase {

    private final IQuotationRepository quotationRepo;
    private final IQuotationPdfService pdfService;
    private final QuotationPdfDataBuilder pdfDataBuilder;
    private final QuotationEmailComposer emailComposer;

    /** @param quotationRepo báo giá @param pdfService sinh PDF @param pdfDataBuilder dựng dữ liệu PDF
     *  @param emailComposer dùng để resolve tên người liên hệ hiển thị trên PDF (giống lúc gửi email) */
    public PreviewQuotationPdfUseCase(IQuotationRepository quotationRepo, IQuotationPdfService pdfService,
                                       QuotationPdfDataBuilder pdfDataBuilder, QuotationEmailComposer emailComposer) {
        this.quotationRepo = quotationRepo;
        this.pdfService = pdfService;
        this.pdfDataBuilder = pdfDataBuilder;
        this.emailComposer = emailComposer;
    }

    /**
     * Sinh PDF xem trước cho một báo giá.
     * @param quotationId ID báo giá
     * @return nội dung file PDF
     */
    public byte[] execute(Long quotationId) {
        Quotation q = quotationRepo.findById(quotationId)
                .orElseThrow(() -> new NotFoundException("Quotation not found: " + quotationId));
        String contactName = emailComposer.draft(q).recipientName();
        return pdfService.render(pdfDataBuilder.build(q, contactName));
    }
}
