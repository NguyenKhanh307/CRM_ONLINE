import { useQuery } from '@tanstack/react-query';
import { quotationService } from '../services/quotationService';

/** Lấy chi tiết một báo giá theo ID (trang chi tiết). */
export function useQuotationDetail(id: number | undefined) {
    return useQuery({
        queryKey: ['quotation', id],
        queryFn: () => quotationService.getById(id as number).then(r => r.data.data),
        enabled: id != null && !Number.isNaN(id),
    });
}
