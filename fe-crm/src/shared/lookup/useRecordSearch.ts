import { keepPreviousData, useQuery } from '@tanstack/react-query';
import { customerService } from '@/features/khach-hang/services/customerService';
import { contactService } from '@/features/lien-he/services/contactService';
import { leadService } from '@/features/tiem-nang/services/leadService';
import { opportunityService } from '@/features/co-hoi/services/opportunityService';
import { quotationService } from '@/features/bao-gia/services/quotationService';
import { orderService } from '@/features/don-hang/services/orderService';
import { invoiceService } from '@/features/hoa-don/services/invoiceService';
import type { PageParams } from '@/shared/types/api';
import type { SelectOption } from '@/shared/components/SearchableSelect';

// phân hệ có thể chọn bằng ô tìm kiếm bản ghi
export type RecordModule =
    | 'customer' | 'contact' | 'lead' | 'opportunity' | 'quotation' | 'order' | 'invoice';

// số bản ghi trả về mỗi lần tìm — vừa đủ một danh sách thả xuống
const PAGE_SIZE = 20;

// cách tìm + cách dựng nhãn của từng phân hệ
// cột backend tìm được (ListQueryUtils.likeClause) khác nhau nên hint nói rõ cho người dùng:
// quotation/order/invoice CHỈ tìm theo mã, còn lead/customer/opportunity tìm cả tên
const SOURCES: Record<RecordModule, {
    hint: string;
    fetch: (params: PageParams) => Promise<SelectOption[]>;
}> = {
    customer: {
        hint: 'Tìm theo mã, tên, MST, SĐT…',
        fetch: (p) => customerService.getList(p).then((r) =>
            r.data.data.items.map((c) => ({ value: String(c.id), label: `${c.code} — ${c.name}` }))),
    },
    contact: {
        hint: 'Tìm theo tên hoặc email',
        fetch: (p) => contactService.getList(p).then((r) =>
            r.data.data.items.map((c) => ({ value: String(c.id), label: c.fullName }))),
    },
    lead: {
        hint: 'Tìm theo mã, tên, công ty, SĐT…',
        fetch: (p) => leadService.getList(p).then((r) =>
            r.data.data.items.map((l) => ({ value: String(l.id), label: `${l.code} — ${l.name}` }))),
    },
    opportunity: {
        hint: 'Tìm theo mã hoặc tên cơ hội',
        fetch: (p) => opportunityService.getList(p).then((r) =>
            r.data.data.items.map((o) => ({ value: String(o.id), label: `${o.code} — ${o.name}` }))),
    },
    quotation: {
        hint: 'Tìm theo mã báo giá (BG…)',
        fetch: (p) => quotationService.getList(p).then((r) =>
            r.data.data.items.map((q) => ({
                value: String(q.id),
                label: q.customerName ? `${q.code} — ${q.customerName}` : q.code,
            }))),
    },
    order: {
        hint: 'Tìm theo mã đơn hàng (DH…)',
        fetch: (p) => orderService.getList(p).then((r) =>
            r.data.data.items.map((o) => ({
                value: String(o.id),
                label: o.customerName ? `${o.code} — ${o.customerName}` : o.code,
            }))),
    },
    invoice: {
        hint: 'Tìm theo mã hóa đơn (HD…)',
        fetch: (p) => invoiceService.getList(p).then((r) =>
            r.data.data.items.map((i) => ({
                value: String(i.id),
                label: i.customerName ? `${i.code} — ${i.customerName}` : i.code,
            }))),
    },
};

// gợi ý đặt trong ô tìm kiếm của từng phân hệ
export const searchHintOf = (module: RecordModule) => SOURCES[module].hint;

// tìm bản ghi phía server cho ô chọn khóa ngoại trong form
// dùng thay cho các hook use*List nạp sẵn một trang khi bảng nguồn lớn (liên hệ / tiềm năng /
// cơ hội / báo giá / đơn hàng / hóa đơn đều hàng chục nghìn dòng): nạp 500 dòng rồi lọc tại chỗ thì
// bản ghi cần tìm gần như chắc chắn nằm ngoài trang đó, người dùng gõ gì cũng "Không tìm thấy"
export function useRecordSearch(
    module: RecordModule | '',
    q: string,
    params?: Pick<PageParams, 'customerId'>,
    enabled = true,
) {
    const query = useQuery({
        queryKey: [module, 'search', q, params?.customerId ?? null],
        queryFn: () => SOURCES[module as RecordModule].fetch({
            q: q || undefined,
            page: 0,
            size: PAGE_SIZE,
            sortBy: 'createdAt',
            sortDir: 'desc',
            ...params,
        }),
        enabled: enabled && !!module,
        placeholderData: keepPreviousData,
    });
    return { options: query.data ?? [], loading: query.isFetching };
}
