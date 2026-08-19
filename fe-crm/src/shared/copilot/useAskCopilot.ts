import { useLiveMutation } from '@/core/data/useLiveMutation';
import { copilotService } from './copilotService';
import type { CopilotAnswer } from './copilotTypes';

// hook gửi câu hỏi tới trợ lý AI và nhận câu trả lời (đã unwrap khỏi ApiResponse)
export function useAskCopilot() {
    return useLiveMutation<string, CopilotAnswer>((question: string) =>
        copilotService.ask({ question }).then((r) => r.data.data));
}
