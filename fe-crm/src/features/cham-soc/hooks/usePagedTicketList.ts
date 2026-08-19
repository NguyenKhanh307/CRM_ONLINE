import { useLiveQuery } from '@/core/data/useLiveQuery';
import type { PageParams } from '@/shared/types/api';
import { ticketService } from '../services/ticketService';

// danh sách phiếu chăm sóc phân trang server-side (search `q` + tag lọc `status` trong PageParams)
export function usePagedTicketList(params: PageParams) {
    return useLiveQuery(`tickets:paged:${JSON.stringify(params)}`, () => ticketService.getList(params).then(r => r.data.data));
}
