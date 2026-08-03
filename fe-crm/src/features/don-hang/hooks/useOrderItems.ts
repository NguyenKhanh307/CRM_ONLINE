import { useQuery } from '@tanstack/react-query';
import { orderService } from '../services/orderService';

// lấy danh sách dòng hàng của một đơn hàng (bảng dưới ở bố cục 2 bảng)
export function useOrderItems(orderId: number | null) {
    return useQuery({
        queryKey: ['order-items', orderId],
        queryFn: () => orderService.getItems(orderId as number).then(r => r.data.data),
        enabled: orderId != null,
    });
}
