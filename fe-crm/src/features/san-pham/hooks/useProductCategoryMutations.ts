import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { productCategoryService, type CreateProductCategoryPayload, type UpdateProductCategoryPayload } from '../services/productCategoryService';

// tạo danh mục sản phẩm mới — báo danh sách danh mục làm mới sau khi thành công
export function useCreateProductCategory() {
    const { mutate: run, isPending } = useLiveMutation((body: CreateProductCategoryPayload) => productCategoryService.create(body));

    const mutate: typeof run = (body, callbacks) =>
        run(body, { ...callbacks, onSuccess: (data) => { notify('product-categories'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}

// cập nhật danh mục sản phẩm — báo danh sách danh mục làm mới sau khi thành công
export function useUpdateProductCategory() {
    const { mutate: run, isPending } = useLiveMutation(
        ({ id, body }: { id: number; body: UpdateProductCategoryPayload }) => productCategoryService.update(id, body));

    const mutate: typeof run = (input, callbacks) =>
        run(input, { ...callbacks, onSuccess: (data) => { notify('product-categories'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}

// xóa danh mục sản phẩm — báo danh sách danh mục làm mới sau khi thành công
export function useDeleteProductCategory() {
    const { mutate: run, isPending } = useLiveMutation((id: number) => productCategoryService.remove(id));

    const mutate: typeof run = (id, callbacks) =>
        run(id, { ...callbacks, onSuccess: (data) => { notify('product-categories'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
