import { useMutation, useQueryClient } from '@tanstack/react-query';
import { leadService } from '../services/leadService';
import type { UpdateLeadPayload } from '../types/leadTypes';

/** Cập nhật tiềm năng — invalidate danh sách sau khi thành công. */
export function useUpdateLead() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: ({ id, payload }: { id: number; payload: UpdateLeadPayload }) =>
            leadService.update(id, payload),
        onSuccess: (_d, v) => {
            qc.invalidateQueries({ queryKey: ['leads'] });
            qc.invalidateQueries({ queryKey: ['lead', v.id] });
        },
    });
}
