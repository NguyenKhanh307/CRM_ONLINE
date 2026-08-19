import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { opportunityService } from '../services/opportunityService';

// xóa cơ hội — báo danh sách làm mới sau khi thành công
export function useDeleteOpportunity() {
    const { mutate: run, isPending } = useLiveMutation((id: number) => opportunityService.remove(id));

    const mutate: typeof run = (id, callbacks) =>
        run(id, { ...callbacks, onSuccess: (data) => { notify('opportunities'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
