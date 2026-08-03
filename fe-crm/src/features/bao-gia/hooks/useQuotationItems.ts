import { useQuery } from '@tanstack/react-query';
import { quotationService } from '../services/quotationService';

// lấy danh sách dòng hàng của một báo giá (bảng dưới ở bố cục 2 bảng)
export function useQuotationItems(quotationId: number | null) {
    return useQuery({
        queryKey: ['quotation-items', quotationId],
        queryFn: () => quotationService.getItems(quotationId as number).then(r => r.data.data),
        enabled: quotationId != null,
    });
}
