import { useLiveQuery } from '@/core/data/useLiveQuery';
import { dashboardService } from '@/features/dashboard/services/dashboardService';

// số cơ hội "treo" theo từng nhân viên (top 8) — chỉ ADMIN/SALES_MANAGER
export function useStalledByOwner(enabled = true) {
    return useLiveQuery('dashboard-stalled-by-owner', () => dashboardService.getStalledByOwner().then((r) => r.data.data), enabled);
}
