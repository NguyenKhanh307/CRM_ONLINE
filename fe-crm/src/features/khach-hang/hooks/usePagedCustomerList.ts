import { useLiveQuery } from '@/core/data/useLiveQuery';
import type { PageParams } from '@/shared/types/api';
import { customerService } from '../services/customerService';

// danh sách khách hàng phân trang server-side (search `q` + tag lọc `status` trong params)
export function usePagedCustomerList(params: PageParams) {
    return useLiveQuery(`customers:paged:${JSON.stringify(params)}`, () => customerService.getList(params).then(r => r.data.data));
}
