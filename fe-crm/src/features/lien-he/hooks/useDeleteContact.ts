import { useMutation, useQueryClient } from '@tanstack/react-query';
import { contactService } from '../services/contactService';

export function useDeleteContact() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (id: number) => contactService.remove(id),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['contacts'] }),
    });
}
