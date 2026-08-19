import { useLiveQuery } from '@/core/data/useLiveQuery';
import { invoiceService } from '../services/invoiceService';

// lấy toàn bộ hóa đơn (tối đa 500 dòng) — dùng cho dropdown chọn hóa đơn
export function useInvoiceList() {
    return useLiveQuery('invoices', () =>
        invoiceService.getList({ page: 0, size: 500, sortBy: 'createdAt', sortDir: 'desc' }).then(r => r.data.data.items));
}
