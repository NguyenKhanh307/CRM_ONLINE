import { useLiveQuery } from '@/core/data/useLiveQuery';
import type { PageParams } from '@/shared/types/api';
import { orderService } from '../services/orderService';

// danh sách đơn hàng phân trang server-side (search `q` + tag lọc `status` trong PageParams)
export function usePagedOrderList(params: PageParams) {
    return useLiveQuery(`orders:paged:${JSON.stringify(params)}`, () => orderService.getList(params).then(r => r.data.data));
}
