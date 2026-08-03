import { useQuery } from '@tanstack/react-query';
import { customerService } from '../services/customerService';

// lấy chi tiết một khách hàng theo ID (trang 360°)
export function useCustomerDetail(id: number | undefined) {
    return useQuery({
        queryKey: ['customer', id],
        queryFn: () => customerService.getById(id as number).then(r => r.data.data),
        enabled: id != null && !Number.isNaN(id),
    });
}
