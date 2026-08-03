import { useQuery } from '@tanstack/react-query';
import { orderService } from '../services/orderService';

// lấy danh sách Đơn hàng (phân trang)
export function useOrderList() {
    return useQuery({
        queryKey: ['orders'],
        queryFn: () => orderService.getList({ page: 0, size: 500, sortBy: 'createdAt', sortDir: 'desc' }).then(r => r.data.data.items),
    });
}
