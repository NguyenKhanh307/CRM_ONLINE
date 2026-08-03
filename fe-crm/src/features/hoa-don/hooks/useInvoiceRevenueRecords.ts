import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { invoiceService } from '../services/invoiceService';
import type { CreateInvoiceRevenueRecordPayload, UpdateInvoiceRevenueRecordPayload } from '../types/invoiceTypes';

// hook quản lý bản ghi doanh số/chia hoa hồng của một hóa đơn: danh sách + thêm/sửa/xóa
// chỉ invalidate ['invoice-revenue-records', invoiceId] — bản ghi doanh số là dữ liệu thông tin
// (chia hoa hồng), không ảnh hưởng total/paymentStatus của hóa đơn nên không cần invalidate
// ['invoices']/['invoice', invoiceId] như useInvoicePayments
export function useInvoiceRevenueRecords(invoiceId: number | null) {
    const qc = useQueryClient();

    const list = useQuery({
        queryKey: ['invoice-revenue-records', invoiceId],
        queryFn: () => invoiceService.getRevenueRecords(invoiceId as number).then((r) => r.data.data),
        enabled: !!invoiceId,
    });

    const invalidate = () => qc.invalidateQueries({ queryKey: ['invoice-revenue-records', invoiceId] });

    const add = useMutation({
        mutationFn: (payload: CreateInvoiceRevenueRecordPayload) => invoiceService.addRevenueRecord(invoiceId as number, payload),
        onSuccess: invalidate,
    });
    const update = useMutation({
        mutationFn: ({ id, payload }: { id: number; payload: UpdateInvoiceRevenueRecordPayload }) =>
            invoiceService.updateRevenueRecord(invoiceId as number, id, payload),
        onSuccess: invalidate,
    });
    const remove = useMutation({
        mutationFn: (id: number) => invoiceService.deleteRevenueRecord(invoiceId as number, id),
        onSuccess: invalidate,
    });

    return { list, add, update, remove };
}
