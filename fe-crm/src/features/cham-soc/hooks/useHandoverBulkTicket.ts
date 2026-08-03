import { useMutation, useQueryClient } from '@tanstack/react-query';
import { ticketService } from '../services/ticketService';

// bàn giao hàng loạt phiếu sang người xử lý khác
export function useHandoverBulkTicket() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (payload: { ids: number[]; toUserId: number; reason?: string }) =>
            ticketService.handoverBulk(payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['tickets'] }),
    });
}
