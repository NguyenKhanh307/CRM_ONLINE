import { useLiveQuery } from '@/core/data/useLiveQuery';
import { orderService } from '../services/orderService';

// lấy toàn bộ đơn hàng (tối đa 500 dòng) — dùng cho dropdown chọn đơn hàng
export function useOrderList() {
    return useLiveQuery('orders', () =>
        orderService.getList({ page: 0, size: 500, sortBy: 'createdAt', sortDir: 'desc' }).then(r => r.data.data.items));
}
