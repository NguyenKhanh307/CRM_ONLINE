import { useMutation, useQueryClient } from '@tanstack/react-query';
import { productService } from '../services/productService';

export function useDeleteProduct() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (id: number) => productService.remove(id),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['products'] }),
    });
}
