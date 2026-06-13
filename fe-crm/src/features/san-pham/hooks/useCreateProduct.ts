import { useMutation, useQueryClient } from '@tanstack/react-query';
import { productService } from '../services/productService';
import type { CreateProductPayload } from '../types/productTypes';

/** Tạo mới sản phẩm — invalidate danh sách sau khi thành công. */
export function useCreateProduct() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (payload: CreateProductPayload) => productService.create(payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['products'] }),
    });
}
