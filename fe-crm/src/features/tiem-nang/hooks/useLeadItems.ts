import { useLiveQuery } from '@/core/data/useLiveQuery';
import { leadService } from '../services/leadService';

// danh sách sản phẩm quan tâm của một tiềm năng (lead_items)
export function useLeadItems(leadId: number | null) {
    return useLiveQuery(`lead-items:${leadId}`, () => leadService.getItems(leadId as number).then(r => r.data.data), leadId != null);
}
