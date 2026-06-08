import { useQuery } from '@tanstack/react-query';
import { activityService } from '../services/activityService';

export function useActivityList() {
    return useQuery({
        queryKey: ['activities'],
        queryFn: () => activityService.getList({ page: 0, size: 100 }).then(r => r.data.data.items),
    });
}
