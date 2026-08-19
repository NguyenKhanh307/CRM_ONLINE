import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { ticketService } from '../services/ticketService';

// bàn giao hàng loạt phiếu sang người xử lý khác — báo danh sách làm mới sau khi thành công
export function useHandoverBulkTicket() {
    const { mutate: run, isPending } = useLiveMutation(
        (payload: { ids: number[]; toUserId: number; reason?: string }) => ticketService.handoverBulk(payload));

    const mutate: typeof run = (payload, callbacks) =>
        run(payload, { ...callbacks, onSuccess: (data) => { notify('tickets'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
