import { useMutation, useQueryClient } from '@tanstack/react-query';
import { contactService } from '../services/contactService';
import type { UpdateContactPayload } from '../types/contactTypes';

export function useUpdateContact() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: ({ id, payload }: { id: number; payload: UpdateContactPayload }) =>
            contactService.update(id, payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['contacts'] }),
    });
}
