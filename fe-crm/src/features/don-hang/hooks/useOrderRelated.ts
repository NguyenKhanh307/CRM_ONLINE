import { useQuery } from '@tanstack/react-query';
import { orderService } from '../services/orderService';

/** Lấy bản ghi liên quan của một đơn hàng (trang chi tiết). */
export function useOrderRelated(id: number | undefined) {
    return useQuery({
        queryKey: ['order', id, 'related'],
        queryFn: () => orderService.getRelated(id as number).then(r => r.data.data),
        enabled: id != null && !Number.isNaN(id),
    });
}
