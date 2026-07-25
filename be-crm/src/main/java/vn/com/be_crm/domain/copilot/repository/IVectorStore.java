package vn.com.be_crm.domain.copilot.repository;

import vn.com.be_crm.domain.copilot.model.VectorHit;

import java.util.List;

/**
 * Port đọc chỉ mục vector ({@code copilot_chunks}) phục vụ tìm kiếm ngữ nghĩa cho trợ lý AI.
 * <p>Cố ý <b>KHÔNG có</b> phương thức ghi: bảng này do {@code tools/indexer/} (Python, chạy tay
 * trên máy dev) sinh ra. Backend production chỉ đọc.
 */
public interface IVectorStore {

    /**
     * Tìm các trích đoạn gần nghĩa nhất với vector câu hỏi.
     *
     * @param queryVector vector của câu hỏi (đã chuẩn hóa L2, cùng số chiều với cột embedding)
     * @param ownerId     lọc theo người phụ trách — {@code null} nghĩa là ADMIN/quản lý, xem tất cả.
     *                    🚨 Đây là RANH GIỚI BẢO MẬT: nhân viên chỉ được thấy chunk của mình
     *                    hoặc chunk dùng chung ({@code owner_id IS NULL}). Khác với {@code /related}
     *                    (nới owner có chủ đích), ở đây người dùng hỏi được bất cứ điều gì nên
     *                    bắt buộc phải lọc.
     * @param topK        số trích đoạn tối đa trả về
     * @param maxDistance ngưỡng khoảng cách cosine — bỏ hit xa hơn ngưỡng này (tránh nhồi
     *                    trích đoạn không liên quan vào prompt)
     * @return danh sách trích đoạn, gần nhất trước; rỗng nếu chưa build chỉ mục
     */
    List<VectorHit> search(float[] queryVector, Long ownerId, int topK, double maxDistance);

    /** @return tổng số chunk đang có trong chỉ mục (0 = chưa chạy indexer) */
    long countChunks();
}
