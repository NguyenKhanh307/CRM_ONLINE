import { useMutation, useQueryClient } from '@tanstack/react-query';
import { leadService } from '../services/leadService';
import type { CreateLeadPayload } from '../types/leadTypes';

/** Tạo mới tiềm năng — invalidate danh sách sau khi thành công. */
export function useCreateLead() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (payload: CreateLeadPayload) => leadService.create(payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['leads'] }),
    });
}
