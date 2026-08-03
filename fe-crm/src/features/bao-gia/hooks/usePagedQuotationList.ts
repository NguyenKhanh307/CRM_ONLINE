import { keepPreviousData, useQuery } from '@tanstack/react-query';
import type { PageParams } from '@/shared/types/api';
import { quotationService } from '../services/quotationService';

// danh sách báo giá phân trang server-side (search `q` + tag lọc `status` trong PageParams)
export function usePagedQuotationList(params: PageParams) {
    return useQuery({
        queryKey: ['quotations', 'paged', params],
        queryFn: () => quotationService.getList(params).then(r => r.data.data),
        placeholderData: keepPreviousData,
    });
}
