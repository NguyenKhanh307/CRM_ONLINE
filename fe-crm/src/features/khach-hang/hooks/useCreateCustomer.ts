import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { customerService } from '../services/customerService';
import type { CreateCustomerPayload } from '../types/customerTypes';

// tạo mới khách hàng — báo danh sách làm mới sau khi thành công
export function useCreateCustomer() {
    const { mutate: run, isPending } = useLiveMutation((payload: CreateCustomerPayload) => customerService.create(payload));

    const mutate: typeof run = (payload, callbacks) =>
        run(payload, { ...callbacks, onSuccess: (data) => { notify('customers'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
