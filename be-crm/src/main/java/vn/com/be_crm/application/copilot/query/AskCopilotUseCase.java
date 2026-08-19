package vn.com.be_crm.application.copilot.query;

import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import vn.com.be_crm.application.copilot.dto.AskCopilotQuery;
import vn.com.be_crm.application.copilot.dto.CopilotAction;
import vn.com.be_crm.application.copilot.dto.CopilotAnswer;
import vn.com.be_crm.application.copilot.dto.CopilotChartData;
import vn.com.be_crm.application.copilot.dto.CopilotChartSegment;
import vn.com.be_crm.application.copilot.dto.CopilotQuerySpec;
import vn.com.be_crm.application.copilot.dto.CopilotStructuredResponse;
import vn.com.be_crm.application.copilot.dto.NlQueryResult;
import vn.com.be_crm.application.copilot.dto.RecordRef;
import vn.com.be_crm.application.copilot.intent.CopilotIntentDetector;
import vn.com.be_crm.application.copilot.intent.CopilotIntentDetector.Intent;
import vn.com.be_crm.core.ai.port.IAiService;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.copilot.repository.ICopilotContextRepository;
import vn.com.be_crm.domain.copilot.repository.INlQueryEngine;
import vn.com.be_crm.core.error.frontend.DomainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Trợ lý AI Copilot (RAG lai): dò lệnh điều hướng trước; nếu là câu hỏi thật thì gom ngữ cảnh
 * từ <b>ba nhánh</b> rồi để mô hình diễn giải/chọn tham số.
 * <ul>
 *   <li><b>SỐ LIỆU</b> ({@link ICopilotContextRepository}) — native SQL tính doanh thu, phễu,
 *       xếp hạng cho 6 chủ đề đã tối ưu sẵn. Chính xác tuyệt đối, luôn là nguồn số liệu chính.</li>
 *   <li><b>NGỮ NGHĨA</b> ({@link SemanticRetriever}) — tìm vector trong {@code copilot_chunks}
 *       để trả lời câu hỏi mô tả/định tính ("khách nào hay phàn nàn giao hàng trễ").</li>
 *   <li><b>NL2SQL có kiểm soát</b> ({@link INlQueryEngine}) — lưới an toàn cho câu hỏi số liệu
 *       MỚI ngoài 6 chủ đề trên (vd "so sánh số cơ hội giữa sale1 và sale2"). LLM chỉ CHỌN tham số
 *       theo schema ({@link CopilotPrompts#buildStructuredSchema()}), không bao giờ tự viết SQL —
 *       {@link INlQueryEngine} tự dựng SQL an toàn từ whitelist rồi tính số THẬT.</li>
 * </ul>
 * Nội dung chỉ dẫn nằm ở {@link CopilotPrompts}.
 */
public class AskCopilotUseCase implements IUseCase<AskCopilotQuery, CopilotAnswer> {

    private static final Logger log = LoggerFactory.getLogger(AskCopilotUseCase.class);

    private final IAiService aiService;
    private final ICopilotContextRepository contextRepo;
    private final SemanticRetriever semanticRetriever;
    private final INlQueryEngine nlQueryEngine;
    private final CopilotIntentDetector intentDetector = new CopilotIntentDetector();
    // parser JSON thuần Java sẵn có trong spring-boot core (KHÔNG cần thêm dependency Jackson —
    // jackson-databind trong pom chỉ ở scope runtime, không compile được ở application layer)
    private final JsonParser jsonParser = JsonParserFactory.getJsonParser();

    // tiêu đề tiếng Việt cho biểu đồ tròn ở /phan-tich theo chủ đề dò được; không khớp -> mặc định
    private static final Map<String, String> CHART_TITLES = Map.of(
            "employee", "Doanh thu theo nhân viên",
            "workload", "Số lượng công việc theo nhân viên",
            "winrate", "Tỉ lệ chốt đơn theo nhân viên",
            "campaign", "Doanh thu theo chiến dịch",
            "customer", "Doanh thu theo khách hàng",
            "product", "Doanh thu theo sản phẩm");
    private static final String CHART_TITLE_DEFAULT = "Doanh thu kỳ này so với kỳ trước";
    // 3 chủ đề so sánh GIỮA NHÂN VIÊN — chỉ ADMIN/quản lý mới xem được (lộ số liệu đồng nghiệp)
    private static final java.util.Set<String> STAFF_COMPARISON_TOPICS = java.util.Set.of("employee", "workload", "winrate");

    /**
     * @param aiService         port gọi mô hình AI
     * @param contextRepo       port gom ngữ cảnh số liệu CRM (SQL)
     * @param semanticRetriever nhánh truy hồi ngữ nghĩa (vector)
     * @param nlQueryEngine     nhánh NL2SQL có kiểm soát (lưới an toàn cho câu hỏi số liệu mới)
     */
    public AskCopilotUseCase(IAiService aiService, ICopilotContextRepository contextRepo,
                             SemanticRetriever semanticRetriever, INlQueryEngine nlQueryEngine) {
        this.aiService = aiService;
        this.contextRepo = contextRepo;
        this.semanticRetriever = semanticRetriever;
        this.nlQueryEngine = nlQueryEngine;
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

        CopilotStructuredResponse structured = askStructured(input, userPrompt);
        // Không gắn nút biểu đồ khi câu trả lời chính là câu từ chối ngoài phạm vi.
        boolean refused = structured.answer() != null && structured.answer().trim().startsWith(CopilotPrompts.OUT_OF_SCOPE);

        // Ưu tiên nhánh NL2SQL nếu LLM cho rằng câu hỏi ánh xạ được vào truy vấn có cấu trúc VÀ
        // spec đó khớp whitelist (nlQueryEngine tự kiểm tra, valid=false thì rơi về nhánh cũ).
        if (structured.queryable() && !refused) {
            NlQueryResult nl = nlQueryEngine.run(structured.queryType(), structured.spec(),
                    input.question(), input.ownerId(), input.isPrivileged());
            if (nl.valid()) {
                CopilotAction action = nl.chart() != null
                        ? CopilotAction.linkWithChart("/phan-tich", "Xem biểu đồ so sánh", nl.chart())
                        : null;
                return new CopilotAnswer(nl.answer(), action);
            }
        }

        // Nhánh cũ (không đổi hành vi): LLM tự trả lời tự do từ context đã gom sẵn.
        CopilotAction action = null;
        if (intent.wantsChart() && !refused) {
            // 3 chủ đề so sánh giữa nhân viên chỉ dành cho ADMIN/quản lý (đúng luật hiện có của
            // bảng xếp hạng trong ctx) -> nhân viên hỏi trúng từ khóa này tự rơi về mặc định,
            // không lộ số liệu đồng nghiệp.
            // Set.of(...).contains(null) ném NPE -> phải chặn null trước khi tra cứu
            String topic = intent.chartTopic() != null
                    && STAFF_COMPARISON_TOPICS.contains(intent.chartTopic()) && !input.isPrivileged()
                    ? null : intent.chartTopic();
            List<CopilotChartSegment> segs = contextRepo.chartData(topic, intent.period(), input.ownerId(), input.isPrivileged());
            String title = CHART_TITLES.getOrDefault(topic, CHART_TITLE_DEFAULT);
            action = CopilotAction.linkWithChart("/phan-tich", "Xem biểu đồ so sánh", new CopilotChartData(title, segs));
        }
        return new CopilotAnswer(structured.answer(), action);
    }

    /**
     * Gọi Gemini structured output rồi parse JSON thành {@link CopilotStructuredResponse}.
     * Model đôi khi lệch schema — parse lỗi thì KHÔNG chặn câu trả lời, coi cả chuỗi thô là answer
     * và queryable=false (cư xử y hệt luồng generate() tự do trước đây).
     *
     * @param input      phạm vi người dùng
     * @param userPrompt nội dung câu hỏi + ngữ cảnh
     * @return phản hồi có cấu trúc (queryable=false nếu parse lỗi)
     */
    private CopilotStructuredResponse askStructured(AskCopilotQuery input, String userPrompt) {
        String systemPrompt = CopilotPrompts.buildStructuredSystemPrompt(input.isPrivileged());
        String raw = aiService.generateJson(systemPrompt, userPrompt, CopilotPrompts.buildStructuredSchema());
        try {
            Map<String, Object> root = jsonParser.parseMap(raw);
            boolean queryable = Boolean.TRUE.equals(root.get("queryable"));
            String queryType = str(root.get("queryType"));
            String answer = str(root.get("answer"));
            CopilotQuerySpec spec = null;
            Object specObj = root.get("spec");
            if (specObj instanceof Map<?, ?> m) {
                @SuppressWarnings("unchecked")
                List<Object> namesRaw = (List<Object>) m.get("employeeNames");
                List<String> names = namesRaw == null ? List.of() : namesRaw.stream().map(this::str).toList();
                spec = new CopilotQuerySpec(str(m.get("module")), str(m.get("metric")), str(m.get("groupBy")),
                        str(m.get("condition")), names, str(m.get("status")));
            }
            return new CopilotStructuredResponse(queryable, queryType, spec,
                    answer == null || answer.isBlank() ? raw : answer);
        } catch (Exception e) {
            log.warn("Không parse được JSON có cấu trúc từ Gemini, dùng nguyên văn làm câu trả lời: {}", e.toString());
            return new CopilotStructuredResponse(false, null, null, raw);
        }
    }

    private String str(Object o) {
        return o == null ? null : o.toString();
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
