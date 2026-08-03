import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { leadService } from '../services/leadService';
import type { UpdateLeadPayload } from '../types/leadTypes';

// cập nhật tiềm năng — báo danh sách VÀ trang chi tiết đang mở làm mới sau khi thành công
export function useUpdateLead() {
    const { mutate: run, isPending } = useLiveMutation(
        ({ id, payload }: { id: number; payload: UpdateLeadPayload }) => leadService.update(id, payload));

    const mutate: typeof run = (input, callbacks) =>
        run(input, {
            ...callbacks,
            onSuccess: (data) => { notify('leads'); notify(`lead:${input.id}`); callbacks?.onSuccess?.(data); },
        });

    return { mutate, isPending };
}
