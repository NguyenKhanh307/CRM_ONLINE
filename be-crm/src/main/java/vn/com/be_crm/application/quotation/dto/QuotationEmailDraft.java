package vn.com.be_crm.application.quotation.dto;

/**
 * Nội dung email báo giá mặc định để FE hiển thị trước khi gửi (người dùng có thể sửa lại).
 *
 * @param toEmail       email người nhận đã resolve (rỗng nếu chưa có KH/liên hệ có email)
 * @param recipientName tên hiển thị người nhận
 * @param subject       tiêu đề email mặc định
 * @param body          nội dung email mặc định (HTML phần message — KHÔNG gồm 3 nút phản hồi)
 */
public record QuotationEmailDraft(String toEmail, String recipientName, String subject, String body) {
}
