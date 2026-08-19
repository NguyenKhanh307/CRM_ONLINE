import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { contactService } from '../services/contactService';

// xóa liên hệ — báo danh sách làm mới sau khi thành công
export function useDeleteContact() {
    const { mutate: run, isPending } = useLiveMutation((id: number) => contactService.remove(id));

    const mutate: typeof run = (id, callbacks) =>
        run(id, { ...callbacks, onSuccess: (data) => { notify('contacts'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
