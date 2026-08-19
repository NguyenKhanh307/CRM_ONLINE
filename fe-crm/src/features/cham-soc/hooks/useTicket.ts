import { useLiveQuery } from '@/core/data/useLiveQuery';
import { ticketService } from '../services/ticketService';

// lấy chi tiết một phiếu theo ID
export function useTicket(id: number | undefined) {
    return useLiveQuery(`ticket:${id}`, () => ticketService.getById(id as number).then(r => r.data.data), id != null);
}
