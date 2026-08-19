import { useLiveQuery } from '@/core/data/useLiveQuery';
import type { PageParams } from '@/shared/types/api';
import { productService } from '../services/productService';

// danh sách sản phẩm phân trang server-side (search `q` + tag lọc `status` trong PageParams)
export function usePagedProductList(params: PageParams) {
    return useLiveQuery(`products:paged:${JSON.stringify(params)}`, () => productService.getList(params).then(r => r.data.data));
}
