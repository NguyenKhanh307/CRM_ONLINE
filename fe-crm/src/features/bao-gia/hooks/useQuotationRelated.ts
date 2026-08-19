import { useLiveQuery } from '@/core/data/useLiveQuery';
import { quotationService } from '../services/quotationService';

// lấy bản ghi liên quan của một báo giá (trang chi tiết)
export function useQuotationRelated(id: number | undefined) {
    const enabled = id != null && !Number.isNaN(id);
    return useLiveQuery(`quotation:${id}:related`, () => quotationService.getRelated(id as number).then(r => r.data.data), enabled);
}
