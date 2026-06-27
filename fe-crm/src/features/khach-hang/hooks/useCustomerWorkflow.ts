import { useMutation, useQueryClient } from '@tanstack/react-query';
import { customerService } from '../services/customerService';

/** Loại hành động chuyển trạng thái khách hàng. */
export type CustomerAction = 'activate' | 'deactivate';

/**
 * Hook thực hiện hành động chuyển trạng thái khách hàng:
 * activate (kích hoạt) / deactivate (ngừng hoạt động).
 */
export function useCustomerWorkflow() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: ({ id, action }: { id: number; action: CustomerAction }) => customerService[action](id),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['customers'] }),
    });
}
