import { useQuery } from '@tanstack/react-query';
import { quotationService } from '../services/quotationService';

/**
 * Lấy nội dung email báo giá mặc định (tiêu đề + nội dung) để hiển thị trong ô soạn trước khi gửi.
 * Chỉ fetch khi `enabled` (mở modal soạn).
 */
export function useQuotationEmailDraft(id: number | null, enabled: boolean) {
    return useQuery({
        queryKey: ['quotation-email-draft', id],
        queryFn: () => quotationService.getEmailDraft(id!).then(r => r.data.data),
        enabled: enabled && id != null,
        staleTime: 0,
    });
}
