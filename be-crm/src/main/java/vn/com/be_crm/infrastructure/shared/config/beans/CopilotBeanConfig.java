package vn.com.be_crm.infrastructure.shared.config.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.be_crm.application.copilot.query.AskCopilotUseCase;
import vn.com.be_crm.application.shared.ai.IAiService;
import vn.com.be_crm.domain.copilot.repository.ICopilotContextRepository;

/**
 * Wire UseCase của module Copilot (trợ lý AI hỏi đáp CRM) qua @Bean.
 */
@Configuration
public class CopilotBeanConfig {

    /**
     * @param aiService   port gọi mô hình AI (Gemini)
     * @param contextRepo port gom ngữ cảnh dữ liệu CRM
     * @return AskCopilotUseCase
     */
    @Bean
    public AskCopilotUseCase askCopilotUseCase(IAiService aiService, ICopilotContextRepository contextRepo) {
        return new AskCopilotUseCase(aiService, contextRepo);
    }
}
