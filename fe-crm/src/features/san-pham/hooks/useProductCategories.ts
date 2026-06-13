import { useQuery } from '@tanstack/react-query';
import { productCategoryService } from '../services/productCategoryService';

/** Lấy danh sách danh mục sản phẩm — dùng cho dropdown danh mục. */
export function useProductCategories(enabled = true) {
    return useQuery({
        queryKey: ['product-categories'],
        queryFn: () => productCategoryService.getList().then((r) => r.data.data.items),
        enabled,
    });
}
