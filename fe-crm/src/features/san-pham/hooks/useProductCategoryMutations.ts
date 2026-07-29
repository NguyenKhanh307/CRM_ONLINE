import { useMutation, useQueryClient } from '@tanstack/react-query';
import { productCategoryService, type CreateProductCategoryPayload, type UpdateProductCategoryPayload } from '../services/productCategoryService';

/** Invalidate cache danh sách danh mục sau mỗi thay đổi. */
const useInvalidateProductCategories = () => {
    const qc = useQueryClient();
    return () => qc.invalidateQueries({ queryKey: ['product-categories'] });
};

/** Tạo danh mục sản phẩm mới. */
export function useCreateProductCategory() {
    const invalidate = useInvalidateProductCategories();
    return useMutation({
        mutationFn: (body: CreateProductCategoryPayload) => productCategoryService.create(body),
        onSuccess: invalidate,
    });
}

/** Cập nhật danh mục sản phẩm. */
export function useUpdateProductCategory() {
    const invalidate = useInvalidateProductCategories();
    return useMutation({
        mutationFn: ({ id, body }: { id: number; body: UpdateProductCategoryPayload }) =>
            productCategoryService.update(id, body),
        onSuccess: invalidate,
    });
}

/** Xóa danh mục sản phẩm. */
export function useDeleteProductCategory() {
    const invalidate = useInvalidateProductCategories();
    return useMutation({
        mutationFn: (id: number) => productCategoryService.remove(id),
        onSuccess: invalidate,
    });
}
