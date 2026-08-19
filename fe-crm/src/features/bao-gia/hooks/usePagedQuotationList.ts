import { useLiveQuery } from '@/core/data/useLiveQuery';
import type { PageParams } from '@/shared/types/api';
import { quotationService } from '../services/quotationService';

// danh sách báo giá phân trang server-side (search `q` + tag lọc `status` trong params)
export function usePagedQuotationList(params: PageParams) {
    return useLiveQuery(`quotations:paged:${JSON.stringify(params)}`, () => quotationService.getList(params).then(r => r.data.data));
}
