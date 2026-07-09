/** Kiểu dữ liệu cho trợ lý AI Copilot. */

/** Body gửi lên khi hỏi trợ lý. */
export interface AskCopilotPayload {
    question: string;
}

/** Kết quả trả lời từ backend. */
export interface CopilotAnswer {
    answer: string;
}

/** Một dòng hội thoại hiển thị trong widget. */
export interface ChatMessage {
    role: 'user' | 'assistant';
    text: string;
}
