import { useQuery } from '@tanstack/react-query';
import { invoiceService } from '../services/invoiceService';

// lấy chi tiết một hóa đơn theo id (trang chi tiết)
export function useInvoiceDetail(id: number | undefined) {
    return useQuery({
        queryKey: ['invoice', id],
        queryFn: () => invoiceService.getById(id as number).then(r => r.data.data),
        enabled: id != null && !Number.isNaN(id),
    });
}
