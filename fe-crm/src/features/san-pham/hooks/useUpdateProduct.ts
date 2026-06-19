import { useMutation, useQueryClient } from '@tanstack/react-query';
import { productService } from '../services/productService';
import type { UpdateProductPayload } from '../types/productTypes';

/** Cập nhật sản phẩm — invalidate danh sách sau khi thành công. */
export function useUpdateProduct() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: ({ id, payload }: { id: number; payload: UpdateProductPayload }) =>
            productService.update(id, payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['products'] }),
    });
}
