import { useLiveQuery } from '@/core/data/useLiveQuery';
import { ticketService } from '../services/ticketService';

// lấy danh sách dòng hàng trả/đổi của một phiếu
export function useTicketReturnItems(ticketId: number | undefined) {
    return useLiveQuery(`ticket-return-items:${ticketId}`, () => ticketService.getReturnItems(ticketId as number).then(r => r.data.data), ticketId != null);
}
