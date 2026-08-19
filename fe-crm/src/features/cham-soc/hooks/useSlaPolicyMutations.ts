import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { slaPolicyService, type CreateSlaPolicyPayload, type UpdateSlaPolicyPayload } from '../services/slaPolicyService';

// tạo chính sách SLA mới — báo danh sách làm mới sau khi thành công
export function useCreateSlaPolicy() {
    const { mutate: run, isPending } = useLiveMutation((body: CreateSlaPolicyPayload) => slaPolicyService.create(body));

    const mutate: typeof run = (body, callbacks) =>
        run(body, { ...callbacks, onSuccess: (data) => { notify('sla-policies'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}

// cập nhật chính sách SLA — báo danh sách làm mới sau khi thành công
export function useUpdateSlaPolicy() {
    const { mutate: run, isPending } = useLiveMutation(
        ({ id, body }: { id: number; body: UpdateSlaPolicyPayload }) => slaPolicyService.update(id, body));

    const mutate: typeof run = (input, callbacks) =>
        run(input, { ...callbacks, onSuccess: (data) => { notify('sla-policies'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}

// xóa chính sách SLA — báo danh sách làm mới sau khi thành công
export function useDeleteSlaPolicy() {
    const { mutate: run, isPending } = useLiveMutation((id: number) => slaPolicyService.remove(id));

    const mutate: typeof run = (id, callbacks) =>
        run(id, { ...callbacks, onSuccess: (data) => { notify('sla-policies'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
