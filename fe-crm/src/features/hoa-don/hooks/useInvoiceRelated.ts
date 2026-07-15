import { useQuery } from '@tanstack/react-query';
import { invoiceService } from '../services/invoiceService';

/** Lấy bản ghi liên quan của một hóa đơn (trang chi tiết). */
export function useInvoiceRelated(id: number | undefined) {
    return useQuery({
        queryKey: ['invoice', id, 'related'],
        queryFn: () => invoiceService.getRelated(id as number).then(r => r.data.data),
        enabled: id != null && !Number.isNaN(id),
    });
}
