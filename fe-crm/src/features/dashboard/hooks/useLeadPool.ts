import { useLiveQuery } from '@/core/data/useLiveQuery';
import { dashboardService } from '@/features/dashboard/services/dashboardService';

// lead đang ở pool chung, chưa ai nhận — chỉ ADMIN/SALES_MANAGER
export function useLeadPool(enabled = true) {
    return useLiveQuery('dashboard-lead-pool', () => dashboardService.getLeadPool().then((r) => r.data.data), enabled);
}
