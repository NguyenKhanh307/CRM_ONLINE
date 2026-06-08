import { useQuery } from '@tanstack/react-query';
import { orderService } from '../services/orderService';

export function useOrderList() {
    return useQuery({
        queryKey: ['orders'],
        queryFn: () => orderService.getList({ page: 0, size: 100 }).then(r => r.data.data.items),
    });
}
