import { useMutation, useQueryClient } from '@tanstack/react-query';
import { orderService } from '../services/orderService';

// xóa Đơn hàng — invalidate danh sách sau khi thành công
export function useDeleteOrder() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (id: number) => orderService.remove(id),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['orders'] }),
    });
}
