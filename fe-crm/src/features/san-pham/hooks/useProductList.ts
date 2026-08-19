import { useLiveQuery } from '@/core/data/useLiveQuery';
import { productService } from '../services/productService';

// lấy danh sách sản phẩm (phân trang)
export function useProductList() {
    // size 1000: bảng sản phẩm đã vượt 500 dòng — nạp thiếu là dropdown dòng hàng âm thầm mất sản phẩm
    return useLiveQuery('products', () =>
        productService.getList({ page: 0, size: 1000, sortBy: 'createdAt', sortDir: 'desc' }).then(r => r.data.data.items));
}
