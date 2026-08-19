import { useLiveQuery } from '@/core/data/useLiveQuery';
import { orderService } from '../services/orderService';

// lấy danh sách dòng hàng của một đơn hàng (bảng dưới ở bố cục 2 bảng)
export function useOrderItems(orderId: number | null) {
    return useLiveQuery(`order-items:${orderId}`, () => orderService.getItems(orderId as number).then(r => r.data.data), orderId != null);
}
