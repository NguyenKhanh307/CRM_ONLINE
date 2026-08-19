import { useLiveQuery } from '@/core/data/useLiveQuery';
import { quotationService } from '../services/quotationService';

// lấy lịch sử phê duyệt của một báo giá (trang chi tiết, tab "Lịch sử duyệt")
export function useQuotationApprovals(id: number | undefined) {
    const enabled = id != null && !Number.isNaN(id);
    return useLiveQuery(`quotation-approvals:${id}`, () => quotationService.getApprovals(id as number).then(r => r.data.data), enabled);
}
