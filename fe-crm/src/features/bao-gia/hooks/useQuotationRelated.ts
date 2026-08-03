import { useQuery } from '@tanstack/react-query';
import { quotationService } from '../services/quotationService';

// lấy bản ghi liên quan của một báo giá (trang chi tiết)
export function useQuotationRelated(id: number | undefined) {
    return useQuery({
        queryKey: ['quotation', id, 'related'],
        queryFn: () => quotationService.getRelated(id as number).then(r => r.data.data),
        enabled: id != null && !Number.isNaN(id),
    });
}
