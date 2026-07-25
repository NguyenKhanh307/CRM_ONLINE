import { useQuery } from '@tanstack/react-query';
import { productService } from '../services/productService';

/** Lấy danh sách sản phẩm (phân trang). */
export function useProductList() {
    return useQuery({
        queryKey: ['products'],
        // size 1000: bảng sản phẩm đã vượt 500 dòng — nạp thiếu là dropdown dòng hàng âm thầm mất sản phẩm
        queryFn: () => productService.getList({ page: 0, size: 1000, sortBy: 'createdAt', sortDir: 'desc' }).then(r => r.data.data.items),
    });
}
