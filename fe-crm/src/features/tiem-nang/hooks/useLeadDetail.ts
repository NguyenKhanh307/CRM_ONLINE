import { useQuery } from '@tanstack/react-query';
import { leadService } from '../services/leadService';

/** Lấy chi tiết một tiềm năng theo ID (trang chi tiết). */
export function useLeadDetail(id: number | undefined) {
    return useQuery({
        queryKey: ['lead', id],
        queryFn: () => leadService.getById(id as number).then(r => r.data.data),
        enabled: id != null && !Number.isNaN(id),
    });
}
