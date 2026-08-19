import { useLiveQuery } from '@/core/data/useLiveQuery';
import { dashboardService } from '@/features/dashboard/services/dashboardService';

// cơ hội "treo" — BE tự giới hạn theo quyền (toàn đội hoặc cá nhân); không truyền days = dùng mặc định BE
export function useStalledOpportunities() {
    return useLiveQuery('dashboard-stalled-opportunities', () => dashboardService.getStalledOpportunities().then((r) => r.data.data));
}
