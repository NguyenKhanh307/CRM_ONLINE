import { useLiveQuery } from '@/core/data/useLiveQuery';
import { quotationService } from '../services/quotationService';

// lấy chi tiết một báo giá theo ID (trang chi tiết)
export function useQuotationDetail(id: number | undefined) {
    const enabled = id != null && !Number.isNaN(id);
    return useLiveQuery(`quotation:${id}`, () => quotationService.getById(id as number).then(r => r.data.data), enabled);
}
