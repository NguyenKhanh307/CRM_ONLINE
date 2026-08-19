import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { customerService } from '../services/customerService';

// loại hành động chuyển trạng thái khách hàng
export type CustomerAction = 'activate' | 'deactivate';

// hook thực hiện hành động chuyển trạng thái khách hàng: activate (kích hoạt) / deactivate (ngừng hoạt động)
export function useCustomerWorkflow() {
    const { mutate: run, isPending } = useLiveMutation(
        ({ id, action }: { id: number; action: CustomerAction }) => customerService[action](id));

    const mutate: typeof run = (input, callbacks) =>
        run(input, {
            ...callbacks,
            onSuccess: (data) => { notify('customers'); notify(`customer:${input.id}`); callbacks?.onSuccess?.(data); },
        });

    return { mutate, isPending };
}
