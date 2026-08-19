import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { opportunityService } from '../services/opportunityService';
import type { UpdateOpportunityPayload } from '../types/opportunityTypes';

// cập nhật cơ hội — báo danh sách VÀ trang chi tiết đang mở làm mới sau khi thành công
export function useUpdateOpportunity() {
    const { mutate: run, isPending } = useLiveMutation(
        ({ id, payload }: { id: number; payload: UpdateOpportunityPayload }) => opportunityService.update(id, payload));

    const mutate: typeof run = (input, callbacks) =>
        run(input, {
            ...callbacks,
            onSuccess: (data) => { notify('opportunities'); notify(`opportunity:${input.id}`); callbacks?.onSuccess?.(data); },
        });

    return { mutate, isPending };
}
