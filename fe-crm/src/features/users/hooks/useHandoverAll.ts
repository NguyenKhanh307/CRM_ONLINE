import { useMutation } from '@tanstack/react-query';
import { userService } from '../services/userService';

export function useHandoverAll() {
    return useMutation({
        mutationFn: (payload: { fromUserId: number; toUserId: number; reason?: string }) =>
            userService.handoverAll(payload),
    });
}
