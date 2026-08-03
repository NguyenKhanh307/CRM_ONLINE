import { useLiveQuery } from '@/core/data/useLiveQuery';
import { leadService } from '../services/leadService';

// lấy chi tiết một tiềm năng theo ID (trang chi tiết)
export function useLeadDetail(id: number | undefined) {
    const enabled = id != null && !Number.isNaN(id);
    return useLiveQuery(`lead:${id}`, () => leadService.getById(id as number).then(r => r.data.data), enabled);
}
