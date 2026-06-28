import { useQuery } from '@tanstack/react-query';
import { quotationService } from '../services/quotationService';

/** Lấy danh sách báo giá (phân trang). */
export function useQuotationList() {
    return useQuery({
        queryKey: ['quotations'],
        queryFn: () => quotationService.getList({ page: 0, size: 500, sortBy: 'createdAt', sortDir: 'desc' }).then(r => r.data.data.items),
    });
}
