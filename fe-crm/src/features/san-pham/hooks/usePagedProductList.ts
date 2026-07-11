import { keepPreviousData, useQuery } from '@tanstack/react-query';
import type { PageParams } from '@/shared/types/api';
import { productService } from '../services/productService';

/** Danh sách sản phẩm phân trang server-side (search `q` + tag lọc `status` trong PageParams). */
export function usePagedProductList(params: PageParams) {
    return useQuery({
        queryKey: ['products', 'paged', params],
        queryFn: () => productService.getList(params).then(r => r.data.data),
        placeholderData: keepPreviousData,
    });
}
