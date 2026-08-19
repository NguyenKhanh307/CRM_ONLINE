import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { ticketService } from '../services/ticketService';
import type { CreateTicketPayload } from '../types/ticketTypes';

// tạo mới phiếu — báo danh sách làm mới sau khi thành công
export function useCreateTicket() {
    const { mutate: run, isPending } = useLiveMutation((payload: CreateTicketPayload) => ticketService.create(payload));

    const mutate: typeof run = (payload, callbacks) =>
        run(payload, { ...callbacks, onSuccess: (data) => { notify('tickets'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
