import { useLiveQuery } from '@/core/data/useLiveQuery';
import type { PageParams } from '@/shared/types/api';
import { leadService } from '../services/leadService';

// danh sách tiềm năng phân trang server-side (search `q` + tag lọc `status` trong params)
export function usePagedLeadList(params: PageParams) {
    return useLiveQuery(`leads:paged:${JSON.stringify(params)}`, () => leadService.getList(params).then(r => r.data.data));
}
