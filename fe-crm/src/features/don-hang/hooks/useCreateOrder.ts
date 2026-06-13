import { useMutation, useQueryClient } from '@tanstack/react-query';
import { orderService } from '../services/orderService';
import type { CreateOrderPayload } from '../types/orderTypes';

/** Tạo mới đơn hàng — invalidate danh sách sau khi thành công. */
export function useCreateOrder() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (payload: CreateOrderPayload) => orderService.create(payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['orders'] }),
    });
}
