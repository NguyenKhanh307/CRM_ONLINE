import { useLiveQuery } from '@/core/data/useLiveQuery';
import { customerService } from '../services/customerService';

// lấy chi tiết một khách hàng theo ID (trang 360°)
export function useCustomerDetail(id: number | undefined) {
    const enabled = id != null && !Number.isNaN(id);
    return useLiveQuery(`customer:${id}`, () => customerService.getById(id as number).then(r => r.data.data), enabled);
}
