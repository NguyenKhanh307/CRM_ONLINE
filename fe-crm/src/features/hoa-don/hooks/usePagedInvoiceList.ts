import { useLiveQuery } from '@/core/data/useLiveQuery';
import type { PageParams } from '@/shared/types/api';
import { invoiceService } from '../services/invoiceService';

// danh sách hóa đơn phân trang server-side (search `q` + tag lọc `status` trong PageParams)
export function usePagedInvoiceList(params: PageParams) {
    return useLiveQuery(`invoices:paged:${JSON.stringify(params)}`, () => invoiceService.getList(params).then(r => r.data.data));
}
