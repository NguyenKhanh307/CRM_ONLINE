import { useQuery } from '@tanstack/react-query';
import { invoiceService } from '../services/invoiceService';

// lấy danh sách Hóa đơn (phân trang)
export function useInvoiceList() {
    return useQuery({
        queryKey: ['invoices'],
        queryFn: () => invoiceService.getList({ page: 0, size: 500, sortBy: 'createdAt', sortDir: 'desc' }).then(r => r.data.data.items),
    });
}
