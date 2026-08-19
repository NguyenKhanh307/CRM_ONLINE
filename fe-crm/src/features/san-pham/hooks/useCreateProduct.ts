import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { productService } from '../services/productService';
import type { CreateProductPayload } from '../types/productTypes';

// tạo mới sản phẩm — báo danh sách làm mới sau khi thành công
export function useCreateProduct() {
    const { mutate: run, isPending } = useLiveMutation((payload: CreateProductPayload) => productService.create(payload));

    const mutate: typeof run = (payload, callbacks) =>
        run(payload, { ...callbacks, onSuccess: (data) => { notify('products'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
