package vn.com.be_crm.application.copilot.query;

import vn.com.be_crm.core.ai.port.IEmbeddingService;
import vn.com.be_crm.domain.copilot.model.VectorHit;
import vn.com.be_crm.domain.copilot.repository.IVectorStore;

import java.util.List;

/**
 * Nhánh truy hồi NGỮ NGHĨA của Copilot: nhúng câu hỏi rồi tìm các thẻ tóm tắt gần nghĩa nhất
 * trong {@code copilot_chunks}.
 * <p>Bổ sung cho nhánh SỐ LIỆU ({@code ICopilotContextRepository}) chứ <b>không thay thế</b>:
 * con số vẫn phải do SQL tính, nhánh này chỉ cấp nội dung mô tả (mô tả cơ hội, lý do thua,
 * nội dung phiếu chăm sóc, phản hồi của khách...) mà SQL tổng hợp không diễn đạt được.
 */
public class SemanticRetriever {

    private final IEmbeddingService embeddingService;
    private final IVectorStore vectorStore;
    private final boolean enabled;
    private final int topK;
    private final double maxDistance;

    /**
     * @param embeddingService port nhúng câu hỏi
     * @param vectorStore      port đọc chỉ mục vector
     * @param enabled          cờ bật/tắt (app.ai.embed.enabled)
     * @param topK             số trích đoạn tối đa đưa vào prompt
     * @param maxDistance      ngưỡng khoảng cách cosine, xa hơn thì loại
     */
    public SemanticRetriever(IEmbeddingService embeddingService, IVectorStore vectorStore,
                             boolean enabled, int topK, double maxDistance) {
        this.embeddingService = embeddingService;
        this.vectorStore = vectorStore;
        this.enabled = enabled;
        this.topK = topK;
        this.maxDistance = maxDistance;
    }

    /**
     * Lấy các trích đoạn liên quan tới câu hỏi, đã format sẵn để nhồi vào prompt.
     * <p>🚨 <b>Degrade an toàn</b>: tắt cờ, hết hạn mức, chưa build chỉ mục hay lỗi mạng đều trả
     * chuỗi rỗng — Copilot vẫn trả lời được bằng nhánh số liệu như trước khi có vector.
     * Không bao giờ ném exception ra ngoài.
     *
     * @param question     câu hỏi người dùng
     * @param ownerId      lọc theo người phụ trách (null = ADMIN/quản lý, xem tất cả)
     * @param isPrivileged true nếu ADMIN/SALES_MANAGER
     * @return khối văn bản trích đoạn, hoặc chuỗi rỗng nếu không có gì phù hợp
     */
    public String retrieve(String question, Long ownerId, boolean isPrivileged) {
        if (!enabled || question == null || question.isBlank()) return "";
        try {
            float[] vec = embeddingService.embed(question.trim());
            List<VectorHit> hits = vectorStore.search(vec, isPrivileged ? null : ownerId,
                    topK, maxDistance);
            if (hits.isEmpty()) return "";
            StringBuilder sb = new StringBuilder();
            for (VectorHit h : hits) {
                sb.append("- ").append(h.content().replace("\n", "\n  ")).append('\n');
            }
            return sb.toString();
        } catch (RuntimeException e) {
            return "";
        }
    }
}
