import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { contactService } from '../services/contactService';
import type { UpdateContactPayload } from '../types/contactTypes';

// cập nhật liên hệ — báo danh sách VÀ trang chi tiết đang mở làm mới sau khi thành công
export function useUpdateContact() {
    const { mutate: run, isPending } = useLiveMutation(
        ({ id, payload }: { id: number; payload: UpdateContactPayload }) => contactService.update(id, payload));

    const mutate: typeof run = (input, callbacks) =>
        run(input, {
            ...callbacks,
            onSuccess: (data) => { notify('contacts'); notify(`contact:${input.id}`); callbacks?.onSuccess?.(data); },
        });

    return { mutate, isPending };
}
