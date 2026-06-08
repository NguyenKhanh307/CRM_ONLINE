import { useQuery } from '@tanstack/react-query';
import { customerService } from '../services/customerService';

export function useCustomerList() {
    return useQuery({
        queryKey: ['customers'],
        queryFn: () => customerService.getList({ page: 0, size: 100 }).then(r => r.data.data.items),
    });
}
