import { useMutation, useQueryClient } from '@tanstack/react-query';
import { ticketService } from '../services/ticketService';
import type { UpdateTicketPayload } from '../types/ticketTypes';

/** Cập nhật phiếu — invalidate danh sách + chi tiết sau khi thành công. */
export function useUpdateTicket() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: ({ id, payload }: { id: number; payload: UpdateTicketPayload }) =>
            ticketService.update(id, payload),
        onSuccess: (_res, { id }) => {
            qc.invalidateQueries({ queryKey: ['tickets'] });
            qc.invalidateQueries({ queryKey: ['ticket', id] });
        },
    });
}
