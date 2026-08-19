import { useLiveQuery } from '@/core/data/useLiveQuery';
import { invoiceService } from '../services/invoiceService';

// lấy chi tiết một hóa đơn theo id (trang chi tiết)
export function useInvoiceDetail(id: number | undefined) {
    const enabled = id != null && !Number.isNaN(id);
    return useLiveQuery(`invoice:${id}`, () => invoiceService.getById(id as number).then(r => r.data.data), enabled);
}
