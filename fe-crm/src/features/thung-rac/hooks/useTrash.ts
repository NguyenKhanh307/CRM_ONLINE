import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { trashService } from '../services/trashService';
import type { TrashModule } from '../types/thungRacTypes';

export function useDeletedItems(module: TrashModule, page = 0, size = 20) {
    return useQuery({
        queryKey: ['trash', module, page, size],
        queryFn: () =>
            trashService.getDeleted(module, { page, size }).then(r => r.data.data),
    });
}

export function useRestore(module: TrashModule) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (id: number) => trashService.restore(module, id),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['trash', module] }),
    });
}

export function usePurge(module: TrashModule) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (id: number) => trashService.purge(module, id),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['trash', module] }),
    });
}
