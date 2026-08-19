// kiểu dữ liệu cho trợ lý AI Copilot

// body gửi lên khi hỏi trợ lý
export interface AskCopilotPayload {
    question: string;
}

// một lát của biểu đồ tròn so sánh ở /phan-tich
export interface CopilotChartSegment {
    label: string;
    value: number;
}

// dữ liệu biểu đồ tròn đính kèm nút "Xem biểu đồ so sánh" — /phan-tich dựng thẳng 1 biểu đồ từ đây,
// không gọi thêm API nào
export interface CopilotChartData {
    title: string;
    segments: CopilotChartSegment[];
}

// hành động đính kèm câu trả lời:
// - navigate: FE tự điều hướng ngay (lệnh "mở trang...")
// - link: hiện nút trong bong bóng chat, bấm mới điều hướng (vd "Xem biểu đồ so sánh")
export interface CopilotAction {
    type: 'navigate' | 'link';
    route: string;
    label: string | null;
    chart: CopilotChartData | null;
}

// kết quả trả lời từ backend
export interface CopilotAnswer {
    answer: string;
    action?: CopilotAction | null;
}

// một dòng hội thoại hiển thị trong widget
export interface ChatMessage {
    role: 'user' | 'assistant';
    text: string;
    action?: CopilotAction | null;
}
