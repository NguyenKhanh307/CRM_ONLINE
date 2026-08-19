import { useLiveQuery } from '@/core/data/useLiveQuery';
import { orderService } from '../services/orderService';

// lấy chi tiết một đơn hàng theo ID (trang chi tiết)
export function useOrderDetail(id: number | undefined) {
    const enabled = id != null && !Number.isNaN(id);
    return useLiveQuery(`order:${id}`, () => orderService.getById(id as number).then(r => r.data.data), enabled);
}
