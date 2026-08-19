import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { productService } from '../services/productService';
import type { UpdateProductPayload } from '../types/productTypes';

// cập nhật sản phẩm — báo danh sách làm mới sau khi thành công
export function useUpdateProduct() {
    const { mutate: run, isPending } = useLiveMutation(
        ({ id, payload }: { id: number; payload: UpdateProductPayload }) => productService.update(id, payload));

    const mutate: typeof run = (input, callbacks) =>
        run(input, { ...callbacks, onSuccess: (data) => { notify('products'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
