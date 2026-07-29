import { useMutation, useQueryClient } from '@tanstack/react-query';
import { orderService } from '../services/orderService';
import type { UpdateOrderPayload } from '../types/orderTypes';

/** Cập nhật Đơn hàng — invalidate danh sách sau khi thành công. */
export function useUpdateOrder() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: ({ id, payload }: { id: number; payload: UpdateOrderPayload }) =>
            orderService.update(id, payload),
        onSuccess: (_d, v) => {
            qc.invalidateQueries({ queryKey: ['orders'] });
            qc.invalidateQueries({ queryKey: ['order', v.id] });
        },
    });
}
