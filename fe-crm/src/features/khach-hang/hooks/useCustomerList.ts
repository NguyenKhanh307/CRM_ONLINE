import { useLiveQuery } from '@/core/data/useLiveQuery';
import { customerService } from '../services/customerService';

// lấy toàn bộ khách hàng (tối đa 500 dòng) — dùng cho dropdown chọn khách hàng
export function useCustomerList() {
    return useLiveQuery('customers', () =>
        customerService.getList({ page: 0, size: 500, sortBy: 'createdAt', sortDir: 'desc' }).then(r => r.data.data.items));
}
