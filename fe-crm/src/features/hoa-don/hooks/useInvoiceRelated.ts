import { useLiveQuery } from '@/core/data/useLiveQuery';
import { invoiceService } from '../services/invoiceService';

// lấy bản ghi liên quan của một hóa đơn (trang chi tiết)
export function useInvoiceRelated(id: number | undefined) {
    const enabled = id != null && !Number.isNaN(id);
    return useLiveQuery(`invoice:${id}:related`, () => invoiceService.getRelated(id as number).then(r => r.data.data), enabled);
}
