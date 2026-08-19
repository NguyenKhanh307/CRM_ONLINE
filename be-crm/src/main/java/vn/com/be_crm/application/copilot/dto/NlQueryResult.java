package vn.com.be_crm.application.copilot.dto;

// kết quả chạy một CopilotQuerySpec đã được validate + thực thi an toàn.
// valid=false nghĩa là spec không khớp whitelist (module/metric/groupBy/condition không hợp lệ)
// -> AskCopilotUseCase rơi về dùng answer tự do của LLM như luồng cũ.
public record NlQueryResult(boolean valid, String answer, CopilotChartData chart) {

    public static NlQueryResult invalid() {
        return new NlQueryResult(false, null, null);
    }
}
