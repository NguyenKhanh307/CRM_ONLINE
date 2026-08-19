import { useLiveQuery } from '@/core/data/useLiveQuery';
import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { invoiceService } from '../services/invoiceService';
import type { PaymentSchedulePayload } from '../types/invoiceTypes';

// hook quản lý đợt thanh toán của một hóa đơn: danh sách + thêm/sửa/xóa
// mọi mutation báo `invoice-payments:{id}` và `invoices` làm mới (be tự suy ra lại
// paymentStatus + status hóa đơn sau mỗi thay đổi)
export function useInvoicePayments(invoiceId: number | null) {
    const list = useLiveQuery(
        `invoice-payments:${invoiceId}`,
        () => invoiceService.getPaymentSchedules(invoiceId as number).then((r) => r.data.data),
        !!invoiceId,
    );

    // báo mọi nơi liên quan làm mới sau khi thêm/sửa/xóa một đợt thanh toán
    const notifyAll = () => {
        notify(`invoice-payments:${invoiceId}`);
        notify('invoices');
        // key SỐ ÍT của trang chi tiết (useInvoiceDetail) — thiếu dòng này thì badge trạng thái /
        // thanh toán trên header đứng yên dù be đã suy lại status sau mỗi thay đổi đợt thanh toán
        notify(`invoice:${invoiceId}`);
    };

    const { mutate: runAdd, isPending: addPending } = useLiveMutation(
        (payload: PaymentSchedulePayload) => invoiceService.addPaymentSchedule(invoiceId as number, payload));
    const add = {
        mutate: (payload: PaymentSchedulePayload, callbacks?: Parameters<typeof runAdd>[1]) =>
            runAdd(payload, { ...callbacks, onSuccess: (data) => { notifyAll(); callbacks?.onSuccess?.(data); } }),
        isPending: addPending,
    };

    const { mutate: runUpdate, isPending: updatePending } = useLiveMutation(
        ({ id, payload }: { id: number; payload: PaymentSchedulePayload }) =>
            invoiceService.updatePaymentSchedule(invoiceId as number, id, payload));
    const update = {
        mutate: (input: { id: number; payload: PaymentSchedulePayload }, callbacks?: Parameters<typeof runUpdate>[1]) =>
            runUpdate(input, { ...callbacks, onSuccess: (data) => { notifyAll(); callbacks?.onSuccess?.(data); } }),
        isPending: updatePending,
    };

    const { mutate: runRemove, isPending: removePending } = useLiveMutation(
        (id: number) => invoiceService.deletePaymentSchedule(invoiceId as number, id));
    const remove = {
        mutate: (id: number, callbacks?: Parameters<typeof runRemove>[1]) =>
            runRemove(id, { ...callbacks, onSuccess: (data) => { notifyAll(); callbacks?.onSuccess?.(data); } }),
        isPending: removePending,
    };

    return { list, add, update, remove };
}
