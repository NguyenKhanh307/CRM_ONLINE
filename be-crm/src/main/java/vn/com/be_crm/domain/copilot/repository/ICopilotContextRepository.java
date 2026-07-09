package vn.com.be_crm.domain.copilot.repository;

/**
 * Port gom ngữ cảnh dữ liệu CRM cho trợ lý AI (RAG) — truy vấn DB thật rồi dựng chuỗi ngữ cảnh.
 * Con số do SQL tính, AI chỉ diễn giải.
 */
public interface ICopilotContextRepository {

    /**
     * Gom ngữ cảnh phục vụ câu hỏi: số liệu tổng hợp (doanh thu, tỷ lệ thắng, phễu…) và — nếu câu hỏi
     * nhắc tới tên/mã bản ghi — phễu của khách hàng liên quan.
     *
     * @param question     câu hỏi người dùng (dùng để suy kỳ + phân giải bản ghi)
     * @param ownerId      lọc theo người phụ trách (null nếu không giới hạn)
     * @param isPrivileged true nếu ADMIN/SALES_MANAGER (xem toàn bộ, ownerId=null)
     * @return chuỗi ngữ cảnh tiếng Việt để nhồi vào prompt
     */
    String assemble(String question, Long ownerId, boolean isPrivileged);
}
