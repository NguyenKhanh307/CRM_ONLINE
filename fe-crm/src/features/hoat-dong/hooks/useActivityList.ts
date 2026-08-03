import { useQuery } from '@tanstack/react-query';
import { activityService } from '../services/activityService';

// lấy danh sách hoạt động (phân trang)
export function useActivityList() {
    return useQuery({
        queryKey: ['activities'],
        queryFn: () => activityService.getList({ page: 0, size: 500, sortBy: 'createdAt', sortDir: 'desc' }).then(r => r.data.data.items),
    });
}
