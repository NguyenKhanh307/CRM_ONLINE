import { useQuery } from '@tanstack/react-query';
import { invoiceService } from '../services/invoiceService';

/** Lấy danh sách Hóa đơn (phân trang). */
export function useInvoiceList() {
    return useQuery({
        queryKey: ['invoices'],
        queryFn: () => invoiceService.getList({ page: 0, size: 100 }).then(r => r.data.data.items),
    });
}
