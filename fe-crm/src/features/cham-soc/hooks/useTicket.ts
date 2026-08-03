import { useQuery } from '@tanstack/react-query';
import { ticketService } from '../services/ticketService';

// lấy chi tiết một phiếu theo ID
export function useTicket(id: number | undefined) {
    return useQuery({
        queryKey: ['ticket', id],
        queryFn: () => ticketService.getById(id as number).then(r => r.data.data),
        enabled: id != null,
    });
}
