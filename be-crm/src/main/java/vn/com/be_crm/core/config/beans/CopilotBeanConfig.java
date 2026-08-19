package vn.com.be_crm.core.config.beans;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.be_crm.application.copilot.query.AskCopilotUseCase;
import vn.com.be_crm.application.copilot.query.SemanticRetriever;
import vn.com.be_crm.core.ai.port.IAiService;
import vn.com.be_crm.core.ai.port.IEmbeddingService;
import vn.com.be_crm.domain.copilot.repository.ICopilotContextRepository;
import vn.com.be_crm.domain.copilot.repository.INlQueryEngine;
import vn.com.be_crm.domain.copilot.repository.IVectorStore;

/**
 * Wire UseCase của module Copilot (trợ lý AI hỏi đáp CRM) qua @Bean.
 */
@Configuration
public class CopilotBeanConfig {

    /**
     * Nhánh truy hồi ngữ nghĩa (vector) — bổ sung cho nhánh số liệu SQL, không thay thế.
     *
     * @param embeddingService port nhúng câu hỏi
     * @param vectorStore      port đọc chỉ mục copilot_chunks
     * @param enabled          app.ai.embed.enabled — tắt thì Copilot chạy y như trước khi có vector
     * @param topK             app.ai.embed.top-k — số trích đoạn tối đa nhồi vào prompt
     * @param maxDistance      app.ai.embed.max-distance — ngưỡng khoảng cách cosine
     * @return SemanticRetriever
     */
    @Bean
    public SemanticRetriever semanticRetriever(IEmbeddingService embeddingService,
                                               IVectorStore vectorStore,
                                               @Value("${app.ai.embed.enabled}") boolean enabled,
                                               @Value("${app.ai.embed.top-k}") int topK,
                                               @Value("${app.ai.embed.max-distance}") double maxDistance) {
        return new SemanticRetriever(embeddingService, vectorStore, enabled, topK, maxDistance);
    }

    /**
     * @param aiService         port gọi mô hình AI (Gemini)
     * @param contextRepo       port gom ngữ cảnh số liệu CRM
     * @param semanticRetriever nhánh truy hồi ngữ nghĩa
     * @param nlQueryEngine     nhánh NL2SQL có kiểm soát (câu hỏi số liệu mới ngoài 6 chủ đề đã tối ưu)
     * @return AskCopilotUseCase
     */
    @Bean
    public AskCopilotUseCase askCopilotUseCase(IAiService aiService,
                                               ICopilotContextRepository contextRepo,
                                               SemanticRetriever semanticRetriever,
                                               INlQueryEngine nlQueryEngine) {
        return new AskCopilotUseCase(aiService, contextRepo, semanticRetriever, nlQueryEngine);
    }
}
