import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { ticketService } from '../services/ticketService';
import type { UpdateTicketPayload } from '../types/ticketTypes';

// cập nhật phiếu — báo danh sách VÀ trang chi tiết đang mở làm mới sau khi thành công
export function useUpdateTicket() {
    const { mutate: run, isPending } = useLiveMutation(
        ({ id, payload }: { id: number; payload: UpdateTicketPayload }) => ticketService.update(id, payload));

    const mutate: typeof run = (input, callbacks) =>
        run(input, {
            ...callbacks,
            onSuccess: (data) => { notify('tickets'); notify(`ticket:${input.id}`); callbacks?.onSuccess?.(data); },
        });

    return { mutate, isPending };
}
