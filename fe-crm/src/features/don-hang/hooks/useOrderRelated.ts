import { useLiveQuery } from '@/core/data/useLiveQuery';
import { orderService } from '../services/orderService';

// lấy bản ghi liên quan của một đơn hàng (trang chi tiết)
export function useOrderRelated(id: number | undefined) {
    const enabled = id != null && !Number.isNaN(id);
    return useLiveQuery(`order:${id}:related`, () => orderService.getRelated(id as number).then(r => r.data.data), enabled);
}
