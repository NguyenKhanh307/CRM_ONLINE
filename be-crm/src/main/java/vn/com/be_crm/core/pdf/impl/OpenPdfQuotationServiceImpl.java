package vn.com.be_crm.core.pdf.impl;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.com.be_crm.core.pdf.port.IQuotationPdfService;
import vn.com.be_crm.core.pdf.port.QuotationPdfData;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Sinh PDF bảng báo giá bằng OpenPDF, dùng font Unicode (TTF) để hiển thị tiếng Việt.
 */
@Component
public class OpenPdfQuotationServiceImpl implements IQuotationPdfService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /** Đường dẫn font Unicode (TTF) — cấu hình app.pdf.font-path. */
    private final String fontPath;

    /** @param fontPath đường dẫn file .ttf hỗ trợ tiếng Việt */
    public OpenPdfQuotationServiceImpl(@Value("${app.pdf.font-path}") String fontPath) {
        this.fontPath = fontPath;
    }

    /**
     * Sinh PDF bảng báo giá: tiêu đề, thông tin khách hàng, bảng dòng hàng, tổng tiền.
     * @param data dữ liệu báo giá @return byte[] nội dung PDF
     */
    @Override
    public byte[] render(QuotationPdfData data) {
        BaseFont baseFont = loadBaseFont();
        Font title = new Font(baseFont, 16, Font.BOLD);
        Font label = new Font(baseFont, 10, Font.BOLD);
        Font normal = new Font(baseFont, 10, Font.NORMAL);
        Font headerCell = new Font(baseFont, 10, Font.BOLD, java.awt.Color.WHITE);

        Document doc = new Document(PageSize.A4, 36, 36, 48, 36);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(doc, baos);
            doc.open();

            Paragraph heading = new Paragraph("BÁO GIÁ " + nz(data.code()), title);
            heading.setAlignment(Element.ALIGN_CENTER);
            heading.setSpacingAfter(12f);
            doc.add(heading);

            doc.add(infoLine("Khách hàng: ", nz(data.customerName()), label, normal));
            doc.add(infoLine("Người liên hệ: ", nz(data.contactName()), label, normal));
            doc.add(infoLine("Ngày báo giá: ", fmtDate(data.quoteDate()), label, normal));
            doc.add(infoLine("Hiệu lực đến: ", fmtDate(data.validUntil()), label, normal));

            Paragraph spacer = new Paragraph(" ", normal);
            spacer.setSpacingAfter(6f);
            doc.add(spacer);

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{6f, 34f, 10f, 10f, 16f, 12f, 18f});
            String[] headers = {"STT", "Sản phẩm", "ĐVT", "SL", "Đơn giá", "Chiết khấu", "Thành tiền"};
            for (String h : headers) table.addCell(headerCell(h, headerCell));

            for (QuotationPdfData.Line ln : data.lines()) {
                table.addCell(bodyCell(String.valueOf(ln.stt()), normal, Element.ALIGN_CENTER));
                table.addCell(bodyCell(nz(ln.productName()), normal, Element.ALIGN_LEFT));
                table.addCell(bodyCell(nz(ln.unit()), normal, Element.ALIGN_CENTER));
                table.addCell(bodyCell(num(ln.quantity()), normal, Element.ALIGN_RIGHT));
                table.addCell(bodyCell(money(ln.unitPrice()), normal, Element.ALIGN_RIGHT));
                table.addCell(bodyCell(money(ln.discount()), normal, Element.ALIGN_RIGHT));
                table.addCell(bodyCell(money(ln.amount()), normal, Element.ALIGN_RIGHT));
            }
            doc.add(table);

            Paragraph totalP = new Paragraph("Tổng cộng: " + money(data.total())
                    + " " + nz(data.currency()), label);
            totalP.setAlignment(Element.ALIGN_RIGHT);
            totalP.setSpacingBefore(10f);
            doc.add(totalP);

            if (data.note() != null && !data.note().isBlank()) {
                Paragraph noteP = new Paragraph("Ghi chú: " + data.note(), normal);
                noteP.setSpacingBefore(10f);
                doc.add(noteP);
            }

            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Không thể sinh PDF báo giá: " + e.getMessage(), e);
        }
    }

    /** Nạp BaseFont Unicode từ đường dẫn cấu hình; ném lỗi rõ nếu thiếu font. */
    private BaseFont loadBaseFont() {
        try {
            if (fontPath == null || !new File(fontPath).exists()) {
                throw new IllegalStateException("Không tìm thấy font PDF tại '" + fontPath
                        + "'. Cấu hình app.pdf.font-path trỏ tới 1 file .ttf hỗ trợ tiếng Việt.");
            }
            return BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Không nạp được font PDF '" + fontPath + "': " + e.getMessage(), e);
        }
    }

    /** Dòng "nhãn: giá trị". */
    private Paragraph infoLine(String labelText, String value, Font label, Font normal) {
        Paragraph p = new Paragraph();
        p.add(new Phrase(labelText, label));
        p.add(new Phrase(value, normal));
        p.setSpacingAfter(2f);
        return p;
    }

    /** Ô tiêu đề bảng (nền xanh). */
    private PdfPCell headerCell(String text, Font font) {
        PdfPCell c = new PdfPCell(new Phrase(text, font));
        c.setBackgroundColor(new java.awt.Color(37, 99, 235));
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        c.setPadding(5f);
        return c;
    }

    /** Ô nội dung bảng. */
    private PdfPCell bodyCell(String text, Font font, int align) {
        PdfPCell c = new PdfPCell(new Phrase(text, font));
        c.setHorizontalAlignment(align);
        c.setPadding(4f);
        return c;
    }

    /** Format ngày dd/MM/yyyy hoặc rỗng. */
    private String fmtDate(LocalDate d) {
        return d == null ? "" : d.format(DATE_FMT);
    }

    /** Format tiền: dấu chấm phân cách nghìn, không phần lẻ. */
    private String money(BigDecimal v) {
        if (v == null) return "0";
        return String.format("%,d", v.setScale(0, java.math.RoundingMode.HALF_UP).longValue()).replace(',', '.');
    }

    /** Format số lượng (bỏ phần lẻ thừa). */
    private String num(BigDecimal v) {
        if (v == null) return "0";
        return v.stripTrailingZeros().toPlainString();
    }

    /** Trả chuỗi rỗng nếu null. */
    private String nz(String s) {
        return s == null ? "" : s;
    }
}
