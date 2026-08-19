import { useLiveQuery } from '@/core/data/useLiveQuery';
import { quotationService } from '../services/quotationService';

// lấy danh sách dòng hàng của một báo giá (bảng dưới ở bố cục 2 bảng)
export function useQuotationItems(quotationId: number | null) {
    return useLiveQuery(
        `quotation-items:${quotationId}`,
        () => quotationService.getItems(quotationId as number).then(r => r.data.data),
        quotationId != null,
    );
}
