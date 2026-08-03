import { useQuery } from '@tanstack/react-query';
import { invoiceService } from '../services/invoiceService';

// lấy danh sách dòng hàng của một hóa đơn (bảng dưới ở bố cục 2 bảng)
export function useInvoiceItems(invoiceId: number | null) {
    return useQuery({
        queryKey: ['invoice-items', invoiceId],
        queryFn: () => invoiceService.getItems(invoiceId as number).then(r => r.data.data),
        enabled: invoiceId != null,
    });
}
