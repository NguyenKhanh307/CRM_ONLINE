import { useMutation } from '@tanstack/react-query';
import { copilotService } from './copilotService';
import type { CopilotAnswer } from './copilotTypes';

/**
 * Hook gửi câu hỏi tới trợ lý AI và nhận câu trả lời (đã unwrap khỏi ApiResponse).
 */
export function useAskCopilot() {
    return useMutation<CopilotAnswer, unknown, string>({
        mutationFn: (question: string) =>
            copilotService.ask({ question }).then((r) => r.data.data),
    });
}
