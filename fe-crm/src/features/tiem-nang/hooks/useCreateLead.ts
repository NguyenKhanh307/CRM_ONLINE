import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { leadService } from '../services/leadService';
import type { CreateLeadPayload } from '../types/leadTypes';

// tạo mới tiềm năng — báo danh sách làm mới sau khi thành công
export function useCreateLead() {
    const { mutate: run, isPending } = useLiveMutation((payload: CreateLeadPayload) => leadService.create(payload));

    const mutate: typeof run = (payload, callbacks) =>
        run(payload, { ...callbacks, onSuccess: (data) => { notify('leads'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
