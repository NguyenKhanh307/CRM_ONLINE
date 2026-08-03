import { useMutation, useQueryClient } from '@tanstack/react-query';
import { customerService } from '../services/customerService';

// loại hành động chuyển trạng thái khách hàng
export type CustomerAction = 'activate' | 'deactivate';

// hook thực hiện hành động chuyển trạng thái khách hàng: activate (kích hoạt) / deactivate (ngừng hoạt động)
export function useCustomerWorkflow() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: ({ id, action }: { id: number; action: CustomerAction }) => customerService[action](id),
        onSuccess: (_d, v) => {
            qc.invalidateQueries({ queryKey: ['customers'] });
            qc.invalidateQueries({ queryKey: ['customer', v.id] });
        },
    });
}
