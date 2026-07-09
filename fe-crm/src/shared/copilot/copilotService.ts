import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse } from '@/shared/types/api';
import type { AskCopilotPayload, CopilotAnswer } from './copilotTypes';

/** Service gọi API trợ lý AI Copilot. */
export const copilotService = {
    /**
     * Hỏi trợ lý AI. Timeout dài hơn mặc định vì mô hình có thể phản hồi chậm.
     * @param payload câu hỏi của người dùng
     */
    ask: (payload: AskCopilotPayload) =>
        axiosInstance.post<ApiResponse<CopilotAnswer>>('/api/copilot/ask', payload, { timeout: 60_000 }),
};
