import { useQuery } from '@tanstack/react-query';
import { quotationService } from '../services/quotationService';

// lấy lịch sử phê duyệt của một báo giá (trang chi tiết, tab "Lịch sử duyệt")
export function useQuotationApprovals(id: number | undefined) {
    return useQuery({
        queryKey: ['quotation-approvals', id],
        queryFn: () => quotationService.getApprovals(id as number).then(r => r.data.data),
        enabled: id != null && !Number.isNaN(id),
    });
}
