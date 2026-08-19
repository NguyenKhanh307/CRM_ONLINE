import { useLiveQuery } from '@/core/data/useLiveQuery';
import { quotationService } from '../services/quotationService';

// lấy toàn bộ báo giá (tối đa 500 dòng) — dùng cho dropdown chọn báo giá
export function useQuotationList() {
    return useLiveQuery('quotations', () =>
        quotationService.getList({ page: 0, size: 500, sortBy: 'createdAt', sortDir: 'desc' }).then(r => r.data.data.items));
}
