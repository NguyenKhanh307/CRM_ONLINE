import { useQuery } from '@tanstack/react-query';
import { productService } from '../services/productService';

export function useProductList() {
    return useQuery({
        queryKey: ['products'],
        queryFn: () => productService.getList({ page: 0, size: 100 }).then(r => r.data.data.items),
    });
}
