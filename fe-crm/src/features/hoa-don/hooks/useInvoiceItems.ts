import { useLiveQuery } from '@/core/data/useLiveQuery';
import { invoiceService } from '../services/invoiceService';

// lấy danh sách dòng hàng của một hóa đơn (bảng dưới ở bố cục 2 bảng)
export function useInvoiceItems(invoiceId: number | null) {
    return useLiveQuery(`invoice-items:${invoiceId}`, () => invoiceService.getItems(invoiceId as number).then(r => r.data.data), invoiceId != null);
}
