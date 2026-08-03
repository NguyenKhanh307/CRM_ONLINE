import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse } from '@/shared/types/api';
import type { AskCopilotPayload, CopilotAnswer } from './copilotTypes';

// service gọi API trợ lý AI Copilot
export const copilotService = {
    // hỏi trợ lý AI — timeout dài hơn mặc định vì mô hình có thể phản hồi chậm
    ask: (payload: AskCopilotPayload) =>
        axiosInstance.post<ApiResponse<CopilotAnswer>>('/api/copilot/ask', payload, { timeout: 60_000 }),
};
