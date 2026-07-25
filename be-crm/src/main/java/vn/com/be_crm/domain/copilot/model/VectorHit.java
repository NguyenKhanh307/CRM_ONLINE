package vn.com.be_crm.domain.copilot.model;

/**
 * Một trích đoạn tìm được bằng tìm kiếm ngữ nghĩa trong {@code copilot_chunks}.
 *
 * @param module   khóa phân hệ (customer/lead/opportunity/quotation/order/invoice/ticket/...)
 * @param recordId ID bản ghi gốc trong bảng nghiệp vụ
 * @param title    tiêu đề thẻ tóm tắt (vd "[Phiếu chăm sóc TK00012] Yêu cầu trả hàng...")
 * @param content  toàn văn thẻ tóm tắt tiếng Việt đã được nhúng
 * @param distance khoảng cách cosine tới câu hỏi — CÀNG NHỎ CÀNG GẦN NGHĨA (0 = trùng khớp)
 */
public record VectorHit(String module, Long recordId, String title, String content,
                        double distance) {
}
