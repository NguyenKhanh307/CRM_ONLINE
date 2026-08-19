import { useLiveMutation } from '@/core/data/useLiveMutation';
import { userService } from '../services/userService';

// bàn giao toàn bộ công việc 5 phân hệ từ một nhân viên sang nhân viên khác
export function useHandoverAll() {
    return useLiveMutation((payload: { fromUserId: number; toUserId: number; reason?: string }) =>
        userService.handoverAll(payload));
}
