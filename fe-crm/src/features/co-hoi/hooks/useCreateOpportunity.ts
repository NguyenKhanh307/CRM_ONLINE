import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { opportunityService } from '../services/opportunityService';
import type { CreateOpportunityPayload } from '../types/opportunityTypes';

// tạo mới cơ hội — báo danh sách làm mới sau khi thành công
export function useCreateOpportunity() {
    const { mutate: run, isPending } = useLiveMutation((payload: CreateOpportunityPayload) => opportunityService.create(payload));

    const mutate: typeof run = (payload, callbacks) =>
        run(payload, { ...callbacks, onSuccess: (data) => { notify('opportunities'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
