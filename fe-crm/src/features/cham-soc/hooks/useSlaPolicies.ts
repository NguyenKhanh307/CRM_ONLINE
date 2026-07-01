import { useQuery } from '@tanstack/react-query';
import { ticketService } from '../services/ticketService';

/** Lấy toàn bộ chính sách SLA. */
export function useSlaPolicies() {
    return useQuery({
        queryKey: ['sla-policies'],
        queryFn: () => ticketService.getSlaPolicies().then(r => r.data.data),
    });
}
