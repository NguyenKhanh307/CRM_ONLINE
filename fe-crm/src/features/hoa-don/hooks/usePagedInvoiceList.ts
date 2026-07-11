import { keepPreviousData, useQuery } from '@tanstack/react-query';
import type { PageParams } from '@/shared/types/api';
import { invoiceService } from '../services/invoiceService';

/** Danh sách hóa đơn phân trang server-side (search `q` + tag lọc `status` trong PageParams). */
export function usePagedInvoiceList(params: PageParams) {
    return useQuery({
        queryKey: ['invoices', 'paged', params],
        queryFn: () => invoiceService.getList(params).then(r => r.data.data),
        placeholderData: keepPreviousData,
    });
}
