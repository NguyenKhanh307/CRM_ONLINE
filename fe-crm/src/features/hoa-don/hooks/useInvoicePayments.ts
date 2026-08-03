import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { invoiceService } from '../services/invoiceService';
import type { PaymentSchedulePayload } from '../types/invoiceTypes';

// hook quản lý đợt thanh toán của một hóa đơn: danh sách + thêm/sửa/xóa
// mọi mutation invalidate ['invoice-payments', invoiceId] và ['invoices'] (be tự suy ra lại
// paymentStatus + status hóa đơn sau mỗi thay đổi)
export function useInvoicePayments(invoiceId: number | null) {
    const qc = useQueryClient();

    const list = useQuery({
        queryKey: ['invoice-payments', invoiceId],
        queryFn: () => invoiceService.getPaymentSchedules(invoiceId as number).then((r) => r.data.data),
        enabled: !!invoiceId,
    });

    const invalidate = () => {
        qc.invalidateQueries({ queryKey: ['invoice-payments', invoiceId] });
        qc.invalidateQueries({ queryKey: ['invoices'] });
        // key SỐ ÍT của trang chi tiết (useInvoiceDetail) — thiếu dòng này thì badge trạng thái /
        // thanh toán trên header đứng yên dù be đã suy lại status sau mỗi thay đổi đợt thanh toán
        qc.invalidateQueries({ queryKey: ['invoice', invoiceId] });
    };

    const add = useMutation({
        mutationFn: (payload: PaymentSchedulePayload) => invoiceService.addPaymentSchedule(invoiceId as number, payload),
        onSuccess: invalidate,
    });
    const update = useMutation({
        mutationFn: ({ id, payload }: { id: number; payload: PaymentSchedulePayload }) =>
            invoiceService.updatePaymentSchedule(invoiceId as number, id, payload),
        onSuccess: invalidate,
    });
    const remove = useMutation({
        mutationFn: (id: number) => invoiceService.deletePaymentSchedule(invoiceId as number, id),
        onSuccess: invalidate,
    });

    return { list, add, update, remove };
}
