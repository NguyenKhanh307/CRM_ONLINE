import { useQuery } from '@tanstack/react-query';
import { ticketService } from '../services/ticketService';

// lấy danh sách dòng hàng trả/đổi của một phiếu
export function useTicketReturnItems(ticketId: number | undefined) {
    return useQuery({
        queryKey: ['ticket-return-items', ticketId],
        queryFn: () => ticketService.getReturnItems(ticketId as number).then(r => r.data.data),
        enabled: ticketId != null,
    });
}
