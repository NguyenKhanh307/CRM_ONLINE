package vn.com.be_crm.application.copilot.dto;

/**
 * Tham số truy vấn cho trợ lý AI Copilot.
 *
 * @param ownerId      người phụ trách để giới hạn dữ liệu (null = không giới hạn, cho ADMIN/Manager)
 * @param isPrivileged true nếu ADMIN/SALES_MANAGER (xem toàn bộ)
 * @param question     câu hỏi của người dùng
 */
public record AskCopilotQuery(Long ownerId, boolean isPrivileged, String question) {
}
