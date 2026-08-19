import { useLiveQuery } from '@/core/data/useLiveQuery';
import { customerService } from '../services/customerService';

// lấy toàn bộ bản ghi liên quan của một khách hàng (trang 360°)
export function useCustomerRelated(id: number | undefined) {
    const enabled = id != null && !Number.isNaN(id);
    return useLiveQuery(`customer:${id}:related`, () => customerService.getRelated(id as number).then(r => r.data.data), enabled);
}
