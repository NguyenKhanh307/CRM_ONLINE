package vn.com.be_crm.domain.copilot.repository;

import vn.com.be_crm.application.copilot.dto.CopilotChartSegment;
import vn.com.be_crm.application.copilot.dto.RecordRef;

import java.util.List;
import java.util.Optional;

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

    /**
     * Tìm một bản ghi theo tên/mã (phục vụ lệnh "mở <module> <tên>") — lấy bản ghi khớp mới cập nhật nhất.
     *
     * @param module       khóa module (customer/lead/contact/opportunity/quotation/order/invoice/campaign/ticket)
     * @param term         cụm tên/mã cần tìm (giữ dấu)
     * @param ownerId      lọc người phụ trách (null = không lọc)
     * @param isPrivileged true nếu ADMIN/SALES_MANAGER (bỏ lọc owner)
     * @return bản ghi khớp nhất nếu có
     */
    Optional<RecordRef> findRecord(String module, String term, Long ownerId, boolean isPrivileged);

    /**
     * Dữ liệu {nhãn, giá trị} cho biểu đồ tròn so sánh ở /phan-tich.
     *
     * @param topic        chủ đề đã dò được ("employee"/"campaign"/"customer"/"product"), null/không
     *                     khớp category nào → trả về 2 lát mặc định "Kỳ này"/"Kỳ trước"
     * @param period       mã kỳ (month/quarter/year)
     * @param ownerId      lọc theo người phụ trách (null nếu không giới hạn)
     * @param isPrivileged true nếu ADMIN/SALES_MANAGER (xem toàn bộ)
     * @return danh sách lát biểu đồ, rỗng nếu không có dữ liệu
     */
    List<CopilotChartSegment> chartData(String topic, String period, Long ownerId, boolean isPrivileged);
}
