package vn.com.be_crm.core.ai.port;

/**
 * Port nhúng (embed) văn bản thành vector — dùng để tìm kiếm ngữ nghĩa trong bảng
 * {@code copilot_chunks}.
 * <p>⚠️ Backend chỉ nhúng <b>câu hỏi</b> của người dùng (1 request/câu hỏi). Việc nhúng dữ liệu
 * CRM do công cụ {@code tools/indexer/} chạy tay trên máy dev đảm nhiệm.
 * <p>🚨 Model và số chiều ở đây <b>bắt buộc trùng</b> với cấu hình của indexer: mỗi model có hệ
 * tọa độ riêng, trộn hai model sẽ khiến khoảng cách cosine vô nghĩa và kết quả tìm kiếm sai
 * <em>một cách âm thầm</em> (không có lỗi nào được ném ra).
 */
public interface IEmbeddingService {

    /**
     * Nhúng một đoạn văn bản thành vector đã chuẩn hóa L2.
     *
     * @param text nội dung cần nhúng (câu hỏi của người dùng)
     * @return vector độ dài {@link #dimensions()}
     */
    float[] embed(String text);

    /** @return số chiều của vector (phải khớp kiểu VECTOR(n) của cột embedding) */
    int dimensions();
}
