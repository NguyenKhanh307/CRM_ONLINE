import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { opportunityStageService, type OpportunityStagePayload } from '../services/opportunityStageService';

// tạo giai đoạn pipeline mới — báo danh sách giai đoạn làm mới sau khi thành công
export function useCreateStage() {
    const { mutate: run, isPending } = useLiveMutation((body: OpportunityStagePayload) => opportunityStageService.create(body));

    const mutate: typeof run = (body, callbacks) =>
        run(body, { ...callbacks, onSuccess: (data) => { notify('opportunity-stages'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}

// cập nhật giai đoạn pipeline — báo danh sách giai đoạn làm mới sau khi thành công
export function useUpdateStage() {
    const { mutate: run, isPending } = useLiveMutation(
        ({ id, body }: { id: number; body: OpportunityStagePayload }) => opportunityStageService.update(id, body));

    const mutate: typeof run = (input, callbacks) =>
        run(input, { ...callbacks, onSuccess: (data) => { notify('opportunity-stages'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}

// xóa giai đoạn pipeline — báo danh sách giai đoạn làm mới sau khi thành công
export function useDeleteStage() {
    const { mutate: run, isPending } = useLiveMutation((id: number) => opportunityStageService.remove(id));

    const mutate: typeof run = (id, callbacks) =>
        run(id, { ...callbacks, onSuccess: (data) => { notify('opportunity-stages'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
