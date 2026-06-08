import { useQuery } from '@tanstack/react-query';
import { leadService } from '../services/leadService';

export function useLeadList() {
    return useQuery({
        queryKey: ['leads'],
        queryFn: () => leadService.getList({ page: 0, size: 100 }).then(r => r.data.data.items),
    });
}
