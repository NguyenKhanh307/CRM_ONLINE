import { useLiveQuery } from '@/core/data/useLiveQuery';
import { productCategoryService } from '../services/productCategoryService';

// lấy danh sách danh mục sản phẩm — dùng cho dropdown danh mục
export function useProductCategories(enabled = true) {
    return useLiveQuery('product-categories', () => productCategoryService.getList().then((r) => r.data.data.items), enabled);
}
