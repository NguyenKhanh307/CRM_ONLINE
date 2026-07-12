import { useQuery } from '@tanstack/react-query';
import { customerService } from '../services/customerService';

/** Lấy toàn bộ bản ghi liên quan của một khách hàng (trang 360°). */
export function useCustomerRelated(id: number | undefined) {
    return useQuery({
        queryKey: ['customer', id, 'related'],
        queryFn: () => customerService.getRelated(id as number).then(r => r.data.data),
        enabled: id != null && !Number.isNaN(id),
    });
}
