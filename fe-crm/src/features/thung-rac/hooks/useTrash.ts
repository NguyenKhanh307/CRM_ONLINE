import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { trashService } from '../services/trashService';
import type { TrashModule } from '../types/thungRacTypes';

/** Lấy danh sách bản ghi đã xóa của một module (phân trang). */
export function useDeletedItems(module: TrashModule, page = 0, size = 20) {
    return useQuery({
        queryKey: ['trash', module, page, size],
        queryFn: () =>
            trashService.getDeleted(module, { page, size }).then(r => r.data.data),
    });
}

/** Khôi phục bản ghi đã xóa từ thùng rác. */
export function useRestore(module: TrashModule) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (id: number) => trashService.restore(module, id),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['trash', module] }),
    });
}

/** Xóa vĩnh viễn bản ghi khỏi thùng rác. */
export function usePurge(module: TrashModule) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (id: number) => trashService.purge(module, id),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['trash', module] }),
    });
}
