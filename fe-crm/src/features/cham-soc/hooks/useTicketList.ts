import { useQuery } from '@tanstack/react-query';
import { ticketService } from '../services/ticketService';

/** Lấy danh sách phiếu hỗ trợ (phân trang, lọc client-side). */
export function useTicketList() {
    return useQuery({
        queryKey: ['tickets'],
        queryFn: () => ticketService.getList({ page: 0, size: 500, sortBy: 'createdAt', sortDir: 'desc' }).then(r => r.data.data.items),
    });
}
