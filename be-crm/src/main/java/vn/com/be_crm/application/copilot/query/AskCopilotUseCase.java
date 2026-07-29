package vn.com.be_crm.application.copilot.query;

import vn.com.be_crm.application.copilot.dto.AskCopilotQuery;
import vn.com.be_crm.application.copilot.dto.CopilotAction;
import vn.com.be_crm.application.copilot.dto.CopilotAnswer;
import vn.com.be_crm.application.copilot.dto.RecordRef;
import vn.com.be_crm.application.copilot.intent.CopilotIntentDetector;
import vn.com.be_crm.application.copilot.intent.CopilotIntentDetector.Intent;
import vn.com.be_crm.application.shared.ai.IAiService;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.copilot.repository.ICopilotContextRepository;
import vn.com.be_crm.domain.shared.exception.DomainException;

import java.util.Optional;

/**
 * Trợ lý AI Copilot (RAG lai): dò lệnh điều hướng trước; nếu là câu hỏi thật thì gom ngữ cảnh
 * từ <b>hai nhánh truy hồi</b> rồi để mô hình diễn giải.
 * <ul>
 *   <li><b>SỐ LIỆU</b> ({@link ICopilotContextRepository}) — native SQL tính doanh thu, phễu,
 *       xếp hạng. Chính xác tuyệt đối, là nguồn DUY NHẤT của mọi con số.</li>
 *   <li><b>NGỮ NGHĨA</b> ({@link SemanticRetriever}) — tìm vector trong {@code copilot_chunks}
 *       để trả lời câu hỏi mô tả/định tính ("khách nào hay phàn nàn giao hàng trễ").</li>
 * </ul>
 * Nội dung chỉ dẫn nằm ở {@link CopilotPrompts}.
 */
public class AskCopilotUseCase implements IUseCase<AskCopilotQuery, CopilotAnswer> {

    private final IAiService aiService;
    private final ICopilotContextRepository contextRepo;
    private final SemanticRetriever semanticRetriever;
    private final CopilotIntentDetector intentDetector = new CopilotIntentDetector();

    /**
     * @param aiService         port gọi mô hình AI
     * @param contextRepo       port gom ngữ cảnh số liệu CRM (SQL)
     * @param semanticRetriever nhánh truy hồi ngữ nghĩa (vector)
     */
    public AskCopilotUseCase(IAiService aiService, ICopilotContextRepository contextRepo,
                             SemanticRetriever semanticRetriever) {
        this.aiService = aiService;
        this.contextRepo = contextRepo;
        this.semanticRetriever = semanticRetriever;
    }

    /**
     * Trả lời câu hỏi của người dùng trong phạm vi quyền được cấp.
     *
     * @param input câu hỏi + phạm vi (owner/quyền)
     * @return câu trả lời, kèm hành động điều hướng/link nếu có
     */
    @Override
    public CopilotAnswer execute(AskCopilotQuery input) {
        if (input == null || input.question() == null || input.question().isBlank()) {
            throw new DomainException("Vui lòng nhập câu hỏi.");
        }
        // Chặn cứng câu hỏi phạm vi admin/hệ thống của NHÂN VIÊN — tất định, không cần gọi AI
        // (bổ sung cho STAFF_SCOPE trong CopilotPrompts, vốn chỉ là chỉ dẫn LLM có thể bị lách qua).
        if (!input.isPrivileged() && intentDetector.isOutOfScopeForStaff(input.question())) {
            return new CopilotAnswer(CopilotPrompts.OUT_OF_SCOPE);
        }

        Intent intent = intentDetector.detect(input.question());
        switch (intent.type()) {
            case OPEN_PAGE:
                return new CopilotAnswer("Đang mở trang " + intent.label() + "...",
                        CopilotAction.navigate(intent.route()));
            case CREATE:
                return new CopilotAnswer("Đang mở form thêm mới " + intent.label() + "...",
                        CopilotAction.navigate(intent.route() + "/them-moi"));
            case OPEN_RECORD:
                return openRecord(intent, input);
            default:
                break;
        }
        String context = contextRepo.assemble(input.question(), input.ownerId(), input.isPrivileged());
        String semantic = semanticRetriever.retrieve(input.question(), input.ownerId(), input.isPrivileged());
        String userPrompt = "DỮ LIỆU:\n" + context
                + (semantic.isEmpty() ? "" : "\nTRÍCH ĐOẠN LIÊN QUAN:\n" + semantic)
                + "\nCÂU HỎI: " + input.question().trim();
        String answer = aiService.generate(CopilotPrompts.buildSystemPrompt(input.isPrivileged()), userPrompt);
        // Không gắn nút biểu đồ khi câu trả lời chính là câu từ chối ngoài phạm vi.
        boolean refused = answer != null && answer.trim().startsWith(CopilotPrompts.OUT_OF_SCOPE);
        CopilotAction action = (intent.wantsChart() && !refused)
                ? CopilotAction.link("/phan-tich?period=" + intent.period(), "Xem biểu đồ so sánh")
                : null;
        return new CopilotAnswer(answer, action);
    }

    /**
     * Xử lý lệnh mở bản ghi cụ thể: tìm theo tên/mã trong phạm vi quyền rồi trả action điều hướng.
     *
     * @param intent ý định đã dò (kèm term)
     * @param input  phạm vi người dùng
     * @return câu trả lời kèm action mở trang chi tiết, hoặc thông báo không tìm thấy
     */
    private CopilotAnswer openRecord(Intent intent, AskCopilotQuery input) {
        Optional<RecordRef> found = contextRepo.findRecord(
                intent.module(), intent.term(), input.ownerId(), input.isPrivileged());
        if (found.isEmpty()) {
            return new CopilotAnswer("Không tìm thấy " + intent.label() + " \"" + intent.term()
                    + "\" trong phạm vi của bạn.");
        }
        RecordRef ref = found.get();
        String display = ref.name() + (ref.code() != null && !ref.code().equals(ref.name())
                ? " (" + ref.code() + ")" : "");
        return new CopilotAnswer("Đang mở " + intent.label() + " " + display + "...",
                CopilotAction.navigate(intent.route() + "/" + ref.id()));
    }
}
