package vn.com.be_crm.application.copilot.query;

import vn.com.be_crm.application.copilot.dto.AskCopilotQuery;
import vn.com.be_crm.application.copilot.dto.CopilotAnswer;
import vn.com.be_crm.application.shared.ai.IAiService;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.copilot.repository.ICopilotContextRepository;
import vn.com.be_crm.domain.shared.exception.DomainException;

/**
 * Trợ lý AI Copilot (RAG): gom ngữ cảnh CRM thật từ DB rồi để mô hình diễn giải và trả lời.
 * Con số do SQL tính, AI không tự tính — nếu thiếu dữ liệu thì trả lời "không có thông tin".
 */
public class AskCopilotUseCase implements IUseCase<AskCopilotQuery, CopilotAnswer> {

    private static final String SYSTEM_PROMPT = """
            Bạn là trợ lý CRM nội bộ, trả lời bằng tiếng Việt, ngắn gọn, chuyên nghiệp.
            CHỈ dựa vào phần DỮ LIỆU được cung cấp để trả lời. Mọi con số đã được hệ thống tính sẵn —
            hãy dùng đúng, KHÔNG tự tính lại hay bịa thêm. Nếu dữ liệu không đủ để trả lời,
            hãy nói rõ là không có thông tin trong hệ thống. Khi có rủi ro (hóa đơn quá hạn,
            phiếu chăm sóc đang mở...) thì nêu bật và gợi ý hành động tiếp theo.""";

    private final IAiService aiService;
    private final ICopilotContextRepository contextRepo;

    /**
     * @param aiService   port gọi mô hình AI
     * @param contextRepo port gom ngữ cảnh dữ liệu CRM
     */
    public AskCopilotUseCase(IAiService aiService, ICopilotContextRepository contextRepo) {
        this.aiService = aiService;
        this.contextRepo = contextRepo;
    }

    /**
     * Gom ngữ cảnh theo phạm vi người dùng rồi gọi AI trả lời câu hỏi.
     *
     * @param input câu hỏi + phạm vi (owner/quyền)
     * @return câu trả lời của trợ lý
     */
    @Override
    public CopilotAnswer execute(AskCopilotQuery input) {
        if (input == null || input.question() == null || input.question().isBlank()) {
            throw new DomainException("Vui lòng nhập câu hỏi.");
        }
        String context = contextRepo.assemble(input.question(), input.ownerId(), input.isPrivileged());
        String userPrompt = "DỮ LIỆU:\n" + context + "\nCÂU HỎI: " + input.question().trim();
        String answer = aiService.generate(SYSTEM_PROMPT, userPrompt);
        return new CopilotAnswer(answer);
    }
}
