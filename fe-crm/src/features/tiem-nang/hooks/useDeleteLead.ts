import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { leadService } from '../services/leadService';

// xóa tiềm năng — báo danh sách làm mới sau khi thành công
export function useDeleteLead() {
    const { mutate: run, isPending } = useLiveMutation((id: number) => leadService.remove(id));

    const mutate: typeof run = (id, callbacks) =>
        run(id, { ...callbacks, onSuccess: (data) => { notify('leads'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
