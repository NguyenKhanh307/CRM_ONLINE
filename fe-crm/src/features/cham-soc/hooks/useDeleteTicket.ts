import { useMutation, useQueryClient } from '@tanstack/react-query';
import { ticketService } from '../services/ticketService';

/** Xóa mềm phiếu — invalidate danh sách sau khi thành công. */
export function useDeleteTicket() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (id: number) => ticketService.remove(id),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['tickets'] }),
    });
}
