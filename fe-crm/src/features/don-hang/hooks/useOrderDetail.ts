import { useQuery } from '@tanstack/react-query';
import { orderService } from '../services/orderService';

// lấy chi tiết một đơn hàng theo ID (trang chi tiết)
export function useOrderDetail(id: number | undefined) {
    return useQuery({
        queryKey: ['order', id],
        queryFn: () => orderService.getById(id as number).then(r => r.data.data),
        enabled: id != null && !Number.isNaN(id),
    });
}
