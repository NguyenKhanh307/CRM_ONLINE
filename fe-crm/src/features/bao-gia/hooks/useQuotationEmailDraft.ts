import { useLiveQuery } from '@/core/data/useLiveQuery';
import { quotationService } from '../services/quotationService';

// lấy nội dung email báo giá mặc định (tiêu đề + nội dung) để hiển thị trong ô soạn trước khi
// gửi — chỉ fetch khi `enabled` (mở modal soạn)
export function useQuotationEmailDraft(id: number | null, enabled: boolean) {
    return useLiveQuery(`quotation-email-draft:${id}`, () => quotationService.getEmailDraft(id!).then(r => r.data.data), enabled && id != null);
}
