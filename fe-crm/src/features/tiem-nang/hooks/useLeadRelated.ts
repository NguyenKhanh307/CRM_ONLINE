import { useQuery } from '@tanstack/react-query';
import { leadService } from '../services/leadService';

/** Lấy bản ghi liên quan của một tiềm năng (trang chi tiết). */
export function useLeadRelated(id: number | undefined) {
    return useQuery({
        queryKey: ['lead', id, 'related'],
        queryFn: () => leadService.getRelated(id as number).then(r => r.data.data),
        enabled: id != null && !Number.isNaN(id),
    });
}
