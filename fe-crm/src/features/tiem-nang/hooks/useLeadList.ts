import { useLiveQuery } from '@/core/data/useLiveQuery';
import { leadService } from '../services/leadService';

// lấy toàn bộ tiềm năng (tối đa 500 dòng) — dùng cho dropdown chọn tiềm năng
export function useLeadList() {
    return useLiveQuery('leads', () =>
        leadService.getList({ page: 0, size: 500, sortBy: 'createdAt', sortDir: 'desc' }).then(r => r.data.data.items));
}
