import { useMutation, useQueryClient } from '@tanstack/react-query';
import { contactService } from '../services/contactService';
import type { CreateContactPayload } from '../types/contactTypes';

/**
 * Tạo mới liên hệ — invalidate danh sách sau khi thành công.
 */
export function useCreateContact() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (payload: CreateContactPayload) => contactService.create(payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['contacts'] }),
    });
}
