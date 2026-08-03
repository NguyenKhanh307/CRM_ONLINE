import { useQuery } from '@tanstack/react-query';
import { customerService } from '../services/customerService';

// lấy danh sách khách hàng (phân trang)
export function useCustomerList() {
    return useQuery({
        queryKey: ['customers'],
        queryFn: () => customerService.getList({ page: 0, size: 500, sortBy: 'createdAt', sortDir: 'desc' }).then(r => r.data.data.items),
    });
}
