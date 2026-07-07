import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { invoiceService } from '../services/invoiceService';
import type { PaymentSchedulePayload } from '../types/invoiceTypes';

/**
 * Hook quản lý đợt thanh toán của một hóa đơn: danh sách + thêm/sửa/xóa.
 * Mọi mutation invalidate ['invoice-payments', invoiceId] và ['invoices']
 * (BE tự suy ra lại paymentStatus + status hóa đơn sau mỗi thay đổi).
 */
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
