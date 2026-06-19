import { useMutation, useQueryClient } from '@tanstack/react-query';
import { leadService } from '../services/leadService';

/** Xóa tiềm năng — invalidate danh sách sau khi thành công. */
export function useDeleteLead() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (id: number) => leadService.remove(id),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['leads'] }),
    });
}
