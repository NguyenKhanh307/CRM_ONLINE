import { keepPreviousData, useQuery } from '@tanstack/react-query';
import type { PageParams } from '@/shared/types/api';
import { ticketService } from '../services/ticketService';

/** Danh sách phiếu chăm sóc phân trang server-side (search `q` + tag lọc `status` trong PageParams). */
export function usePagedTicketList(params: PageParams) {
    return useQuery({
        queryKey: ['tickets', 'paged', params],
        queryFn: () => ticketService.getList(params).then(r => r.data.data),
        placeholderData: keepPreviousData,
    });
}
