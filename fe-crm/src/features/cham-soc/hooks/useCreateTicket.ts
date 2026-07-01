import { useMutation, useQueryClient } from '@tanstack/react-query';
import { ticketService } from '../services/ticketService';
import type { CreateTicketPayload } from '../types/ticketTypes';

/** Tạo mới phiếu — invalidate danh sách sau khi thành công. */
export function useCreateTicket() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (payload: CreateTicketPayload) => ticketService.create(payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['tickets'] }),
    });
}
