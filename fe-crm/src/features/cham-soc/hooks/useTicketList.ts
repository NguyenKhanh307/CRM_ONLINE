import { useLiveQuery } from '@/core/data/useLiveQuery';
import { ticketService } from '../services/ticketService';

// lấy danh sách phiếu hỗ trợ (phân trang, lọc client-side)
export function useTicketList() {
    return useLiveQuery('tickets', () =>
        ticketService.getList({ page: 0, size: 500, sortBy: 'createdAt', sortDir: 'desc' }).then(r => r.data.data.items));
}
