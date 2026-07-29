import { useMutation, useQueryClient } from '@tanstack/react-query';
import { contactService } from '../services/contactService';
import type { UpdateContactPayload } from '../types/contactTypes';

/** Cập nhật liên hệ — invalidate danh sách sau khi thành công. */
export function useUpdateContact() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: ({ id, payload }: { id: number; payload: UpdateContactPayload }) =>
            contactService.update(id, payload),
        onSuccess: (_d, v) => {
            qc.invalidateQueries({ queryKey: ['contacts'] });
            qc.invalidateQueries({ queryKey: ['contact', v.id] });
        },
    });
}
