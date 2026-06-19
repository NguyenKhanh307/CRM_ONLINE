import { useMutation, useQueryClient } from '@tanstack/react-query';
import { contactService } from '../services/contactService';

/** Xóa liên hệ — invalidate danh sách sau khi thành công. */
export function useDeleteContact() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (id: number) => contactService.remove(id),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['contacts'] }),
    });
}
