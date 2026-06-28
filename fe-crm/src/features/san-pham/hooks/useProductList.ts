import { useQuery } from '@tanstack/react-query';
import { productService } from '../services/productService';

/** Lấy danh sách sản phẩm (phân trang). */
export function useProductList() {
    return useQuery({
        queryKey: ['products'],
        queryFn: () => productService.getList({ page: 0, size: 500, sortBy: 'createdAt', sortDir: 'desc' }).then(r => r.data.data.items),
    });
}
