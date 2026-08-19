import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { contactService } from '../services/contactService';
import type { CreateContactPayload } from '../types/contactTypes';

// tạo mới liên hệ — báo danh sách làm mới sau khi thành công
export function useCreateContact() {
    const { mutate: run, isPending } = useLiveMutation((payload: CreateContactPayload) => contactService.create(payload));

    const mutate: typeof run = (payload, callbacks) =>
        run(payload, { ...callbacks, onSuccess: (data) => { notify('contacts'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
