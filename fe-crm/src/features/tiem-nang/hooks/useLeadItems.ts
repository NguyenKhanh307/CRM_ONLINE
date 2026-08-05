import { useQuery } from '@tanstack/react-query';
import { leadService } from '../services/leadService';

// danh sách sản phẩm quan tâm của một tiềm năng (lead_items)
export function useLeadItems(leadId: number | null) {
    return useQuery({
        queryKey: ['lead-items', leadId],
        queryFn: () => leadService.getItems(leadId as number).then(r => r.data.data),
        enabled: leadId != null,
    });
}
