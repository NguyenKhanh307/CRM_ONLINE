/** Kiểu dữ liệu cho trợ lý AI Copilot. */

/** Body gửi lên khi hỏi trợ lý. */
export interface AskCopilotPayload {
    question: string;
}

/**
 * Hành động đính kèm câu trả lời:
 * - `navigate`: FE tự điều hướng ngay (lệnh "mở trang...").
 * - `link`: hiện nút trong bong bóng chat, bấm mới điều hướng (vd "Xem biểu đồ so sánh").
 */
export interface CopilotAction {
    type: 'navigate' | 'link';
    route: string;
    label: string | null;
}

/** Kết quả trả lời từ backend. */
export interface CopilotAnswer {
    answer: string;
    action?: CopilotAction | null;
}

/** Một dòng hội thoại hiển thị trong widget. */
export interface ChatMessage {
    role: 'user' | 'assistant';
    text: string;
    action?: CopilotAction | null;
}
