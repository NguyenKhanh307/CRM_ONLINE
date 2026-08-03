package vn.com.be_crm.core.pdf.port;

/**
 * Port sinh file PDF bảng báo giá — application layer không biết chi tiết thư viện PDF.
 */
public interface IQuotationPdfService {

    /**
     * Sinh nội dung PDF bảng báo giá.
     *
     * @param data dữ liệu báo giá (header + dòng hàng)
     * @return nội dung file PDF dạng byte[]
     */
    byte[] render(QuotationPdfData data);
}
