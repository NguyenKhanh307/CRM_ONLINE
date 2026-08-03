import { useLiveQuery } from '@/core/data/useLiveQuery';
import { leadService } from '../services/leadService';

// lấy bản ghi liên quan của một tiềm năng (trang chi tiết)
export function useLeadRelated(id: number | undefined) {
    const enabled = id != null && !Number.isNaN(id);
    return useLiveQuery(`lead:${id}:related`, () => leadService.getRelated(id as number).then(r => r.data.data), enabled);
}
