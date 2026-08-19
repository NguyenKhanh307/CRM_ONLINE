import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { productService } from '../services/productService';

// xóa sản phẩm — báo danh sách làm mới sau khi thành công
export function useDeleteProduct() {
    const { mutate: run, isPending } = useLiveMutation((id: number) => productService.remove(id));

    const mutate: typeof run = (id, callbacks) =>
        run(id, { ...callbacks, onSuccess: (data) => { notify('products'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
